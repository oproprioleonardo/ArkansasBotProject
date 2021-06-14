package com.leonardo.arkansasproject.database;

import com.google.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class JpaRepository<O, T extends Serializable> implements Repository<O, T> {

    @Getter
    private final Class<O> target;
    @Inject
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private SessionFactory sessionFactory;

    public JpaRepository(Class<O> target) {
        this.target = target;
    }

    @Override
    public void commit(O obj) {
        final Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.save(obj);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public Optional<O> read(T id) {
        Optional<O> opt;
        final Session s = sessionFactory.openSession();
        s.getTransaction().begin();
        try {
            final O obj = s.get(target, id);
            s.getTransaction().commit();;
            opt = Optional.of(obj);
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao requisitar no banco de dados a entidade " + getTarget().getSimpleName() + " de id " + id);
            s.getTransaction().rollback();
            opt = Optional.empty();
        } finally {
            s.close();
        }
        return opt;
    }

    @Override
    public void update(O obj) {
        final Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.update(obj);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void delete(O obj) {
        final Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.remove(obj);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public Optional<O> findAndDelete(T id) {
        final Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        final Optional<O> o = Optional.of(session.load(target, id));
        o.ifPresent(session::delete);
        session.getTransaction().commit();
        session.close();
        return o;
    }

    @Override
    public List<O> findAll() {
        return sessionFactory.openSession().createQuery("FROM " + target.getName(), target).getResultList();
    }

    @Override
    public List<O> findAll(Predicate<O> predicate) {
        return findAll().stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public boolean exists(T id) {
        final Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        boolean result = false;
        try {
            session.load(target, id);
            result = true;
        } catch (Exception ignored) {
        }
        session.getTransaction().commit();
        session.close();
        return result;
    }

}
