package com.leonardo.arkansasproject.listeners;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.leonardo.arkansasproject.dispatchers.*;
import com.leonardo.arkansasproject.entities.Report;
import com.leonardo.arkansasproject.managers.ReportProcessingManager;
import com.leonardo.arkansasproject.report.ReportProcessing;
import com.leonardo.arkansasproject.report.ReportStatus;
import com.leonardo.arkansasproject.services.ReportService;
import com.leonardo.arkansasproject.utils.BotConfig;
import com.leonardo.arkansasproject.utils.Commons;
import com.leonardo.arkansasproject.utils.TemplateMessage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.interactions.UpdateInteractionAction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ButtonClickListener extends ListenerAdapter {

    private static final String separator = "XXXX";
    @Inject
    private ReportProcessingManager manager;
    @Inject
    private ReportService service;
    @Inject
    private Dispatcher dispatcher;
    @Inject
    private BotConfig botConfig;

    private void buildBugButton(String pattern, UpdateInteractionAction action, Long id) {
        this.botConfig.categories
                .stream()
                .filter(bugCategory -> bugCategory.getId().equalsIgnoreCase(pattern))
                .findFirst().ifPresent(bugCategory -> {
            final Set<Component> set = bugCategory.getBugs().stream().map(bug -> Button
                    .primary(id + separator + bug.getId(), bug.getDescription())).collect(
                    Collectors.toSet());
            action.setActionRow(set).queue();
        });
    }

    private void runUpdateTask(String pattern, User author, Long id) {
        this.botConfig.categories.stream().map(BugCategory::getBugs).reduce((bugs, bugs2) -> {
            final HashSet<Bug> bugs1 = Sets.newHashSet(bugs);
            bugs1.addAll(bugs2);
            return bugs1;
        }).flatMap(bugs -> bugs.stream()
                               .filter(bug -> bug.getId().equalsIgnoreCase(pattern))
                               .findFirst()).ifPresent(bug -> this
                .updateStatus(id, author, ReportStatus.ACCEPTED, bug));
    }

    private void updateReportInfoMessage(Long id, Message message, User user, boolean isPrivateMessage) {
        final JDA jda = user.getJDA();
        this.service.read(id)
                    .invoke(report -> {
                        final ReportDispatchInfo info =
                                ReportDispatch.fromReportStatus(report.getStatus()).getInfo();
                        report.getAuthor(jda)
                              .and(jda.retrieveUserById(report.getLastOperator()), (author, lastOperator) -> {
                                  MessageAction action = message.editMessageEmbeds(
                                          Commons.buildInfoMsgFrom(report, author, lastOperator, info.getColorMessage())
                                                 .build());
                                  if (isPrivateMessage) action =
                                          action.setActionRow(
                                                  Button.success("update-report-" + report.getId(), "Atualizar"));
                                  else action = action.setActionRow(
                                          Button.success("update-report-" + report.getId(), "Atualizar"),
                                          Button.secondary("update-report-status-" + report.getId(), "Editar status"));
                                  return action;
                              }).queue(RestAction::queue);
                    }).await().indefinitely();
    }

    private void updateStatus(long id, User author, ReportStatus status, Bug... bugs) {
        this.service.read(id).chain(report -> {
            report.setStatus(status);
            report.setLastOperator(author.getId());
            return this.service.update(report);
        }).invoke(report -> this.dispatcher.dispatch(ReportDispatch.fromReportStatus(status), report, bugs)).await()
                    .indefinitely();
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        final User user = event.getUser();
        final JDA jda = user.getJDA();
        final MessageChannel channel = event.getMessageChannel();
        final Message message = event.getMessage();
        final String componentId = event.getComponentId();
        final ButtonInteraction interaction = event.getInteraction();
        if (message == null) return;
        if ("confirm-next".equals(componentId)) {
            if (!manager.exists(user.getIdLong())) return;
            final ReportProcessing reportProcessing = manager.get(user.getIdLong());
            final Report report = reportProcessing.getReport();
            if (report.getSteps().isEmpty()) {
                channel.sendMessageEmbeds(TemplateMessage.NO_STEPS.getMessageEmbed()).complete().delete()
                       .queueAfter(12, TimeUnit.SECONDS);
            } else {
                message.delete().queue();
                reportProcessing.message =
                        channel
                                .sendMessage(reportProcessing.buildMessage(user))
                                .complete();

                reportProcessing.next(user, (rp, hasNext) -> rp.message = rp.message.editMessage(
                        "**" + user.getName() +
                        "**, agora informe-nos o que deveria acontecer se não existisse o bug, ou seja, o resultado esperado!")
                                                                                    .complete());
            }
        } else if (componentId.contains("-")) {
            final String[] strings = componentId.split("-");
            final UpdateInteractionAction deferEdit = interaction.deferEdit();
            final Function<Integer, Long> parseLong = (position) -> Long.parseLong(strings[position]);
            if (componentId.startsWith("cancel-action-")) {
                deferEdit.queue();
                updateReportInfoMessage(parseLong.apply(2), message, user, false);
            } else if (componentId.startsWith("update-report-")) {
                deferEdit.queue();
                if (strings.length == 3) {
                    updateReportInfoMessage(parseLong.apply(2), message, user, Arrays.stream(ReportDispatch.values())
                                                                                     .noneMatch(rd -> rd.getInfo()
                                                                                                        .getChannelId()
                                                                                                        .equals(channel.getId())));
                } else if (componentId.startsWith("update-report-status-") && strings.length == 4) {
                    this.service.read(parseLong.apply(3)).invoke(report -> {
                        final ReportDispatchInfo info =
                                ReportDispatch.fromReportStatus(report.getStatus()).getInfo();
                        report.getAuthor(jda)
                              .and(jda.retrieveUserById(report.getLastOperator()), (user1, lastOperator) -> {
                                  final MessageAction action = message
                                          .editMessage(
                                                  Commons.buildInfoMsgFrom(report, user1, lastOperator,
                                                                           info.getColorMessage()).build());
                                  switch (report.getStatus()) {
                                      case ACTIVATED:
                                          action.setActionRow(
                                                  Button.success("update-report-status-accepted-" + report.getId(),
                                                                 "Aprovar"),
                                                  Button.danger("update-report-status-refused-" + report.getId(),
                                                                "Recusar"),
                                                  Button.secondary("cancel-action-" + report.getId(),
                                                                   "Cancelar")
                                          ).queue();
                                          break;
                                      case ACCEPTED:
                                          action.setActionRow(
                                                  Button.secondary("update-report-status-archived-" + report.getId(),
                                                                   "Arquivar"),
                                                  Button.danger("cancel-action-" + report.getId(),
                                                                "Cancelar")
                                          ).queue();
                                          break;
                                      case ARCHIVED:
                                      case REFUSED:
                                          action.setActionRow(
                                                  Button.secondary("update-report-status-activated-" + report.getId(),
                                                                   "Voltar a análise"),
                                                  Button.danger("cancel-action-" + report.getId(),
                                                                "Cancelar")
                                          ).queue();
                                          break;
                                  }
                                  return action;
                              }).queue(RestAction::queue);
                    }).await().indefinitely();
                } else if (componentId.startsWith("update-report-status-")) {
                    final Long id = parseLong.apply(4);
                    switch (strings[3]) {
                        case "accepted":
                            this.service.read(id)
                                        .invoke(report -> {
                                                    final Set<Component> set = this.botConfig.categories
                                                            .stream()
                                                            .map(bugCategory -> Button
                                                                    .primary(
                                                                            id +
                                                                            separator +
                                                                            bugCategory
                                                                                    .getId(),
                                                                            bugCategory
                                                                                    .getLabel()))
                                                            .collect(Collectors.toSet());
                                                    report.getAuthor(jda)
                                                          .and(jda.retrieveUserById(report.getLastOperator()),
                                                               (author, lastOperator) -> {
                                                                   final MessageAction action =
                                                                           message.editMessageEmbeds(
                                                                                   Commons.buildInfoMsgFrom(
                                                                                           report,
                                                                                           author,
                                                                                           lastOperator)
                                                                                          .build());

                                                                   return action.setActionRow(set);
                                                               }).queue(RestAction::queue);
                                                }
                                        ).await().indefinitely();
                            return;
                        case "refused":
                        case "archived":
                        case "activated":
                            this.updateStatus(id, user, ReportStatus.valueOf(strings[3].toUpperCase()));
                            break;
                    }
                    message.delete().queue();
                }
            } else if (componentId.contains(separator) && componentId.split(separator).length == 2) {
                final String[] args = componentId.split(separator);
                final long id = Long.parseLong(args[0]);
                final String pattern = args[1];
                if (this.botConfig.categories.stream().anyMatch(
                        bugCategory -> bugCategory.getId().equalsIgnoreCase(pattern)))
                    this.buildBugButton(pattern, deferEdit, id);
                else {
                    this.runUpdateTask(pattern, user, id);
                    message.delete().queue();
                }

            }
        }
    }

}
