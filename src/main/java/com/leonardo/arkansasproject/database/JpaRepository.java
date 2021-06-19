package com.leonardo.arkansasproject.database;

import com.google.inject.Inject;
import io.smallrye.mutiny.Uni;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.mutiny.Mutiny.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class JpaRepository<O, T extends Serializable> implements Repository<O, T> {

    @Getter
    private final Class<O> target;
    @Inject
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Mutiny.SessionFactory sessionFactory;

    public JpaRepository(Class<O> target) {
        this.target = target;
    }

    public Uni<Void> commit(O obj) {
        final Session session = sessionFactory.openSession();
        return session.persist(obj).invoke(session::close);
    }

    public Uni<O> read(T id) {
        final Session session = sessionFactory.openSession();
        return session.find(getTarget(), id).invoke(session::close);
    }


    public Uni<O> update(O obj) {
        final Session session = sessionFactory.openSession();
        return session.merge(obj).invoke(session::close);
    }

    public Uni<Void> delete(O obj) {
        final Session session = sessionFactory.openSession();
        return session.remove(obj).invoke(session::close);
    }

    public Uni<O> deleteById(T id) {
        final Session session = sessionFactory.openSession();
        return read(id).call(session::remove).invoke(session::close);
    }

    public Uni<List<O>> findAll() {
        final Session session = sessionFactory.openSession();
        return session.createQuery("FROM " + target.getName(), target).getResultList().invoke(session::close);
    }

    public Uni<List<O>> findAll(Predicate<O> predicate) {
        return findAll().map(list -> list.stream().filter(predicate).collect(Collectors.toList()));
    }

    public Uni<Boolean> exists(T id) {
        final Session session = sessionFactory.openSession();
        final CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        final CriteriaQuery<O> query = builder.createQuery(getTarget());
        final Root<O> root = query.from(getTarget());
        query.select(root).where(builder.equal(root.get("id"), id));
        final Uni<List<O>> listUni = session.createQuery(query).getResultList();
        return listUni.map(List::isEmpty);
    }

}
