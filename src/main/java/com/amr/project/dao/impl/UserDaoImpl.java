package com.amr.project.dao.impl;

import com.amr.project.dao.abstracts.UserDao;
import com.amr.project.model.entity.Shop;
import com.amr.project.model.entity.User;
import com.amr.project.model.enums.Status;
import com.amr.project.util.QueryResultWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl extends ReadWriteDaoImpl<User, Long> implements UserDao {

    private final EntityManager entityManager;

    @Override
    public User findByUsername(String username) throws NoResultException {
        TypedQuery<User> query = entityManager.createQuery("from User where username = :username", User.class);
        query.setParameter("username", username);
        return QueryResultWrapper.wrapGetSingleResult(query);
    }

    @Override
    public User findByEmail(String email) throws NoResultException {
        TypedQuery<User> query = entityManager.createQuery("from User where email = :email", User.class);
        query.setParameter("email", email);
        return QueryResultWrapper.wrapGetSingleResult(query);
    }


    @Override
    public User findByPhone(String phone) throws NoResultException {
        TypedQuery<User> query = entityManager.createQuery("from User where phone = :phone", User.class);
        query.setParameter("phone", phone);
        return QueryResultWrapper.wrapGetSingleResult(query);
    }

    @Override
    public List<User> findByRole(String role) throws NoResultException {
        TypedQuery<User> query = entityManager.createQuery(
                "select u from User u join u.roles r where r.name=:role", User.class);
        query.setParameter("role", role);
        return query.getResultList();
    }

    @Override
    public int deactivateUser(long id) {
        Query query = entityManager.createQuery(
                        "update User set activate = false where id =:id")
                .setParameter("id", id);
        return query.executeUpdate();
    }

    @Override
    public List<User> findByStatusOrder(Status status) {
        TypedQuery<User> query = entityManager.createQuery(
                "select distinct u from User u join u.orders o where o.status=:status", User.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<User> findByStatusOrderAndShopOwnerUser(Status status, User user) {
        TypedQuery<User> query = entityManager.createQuery(
                "select distinct u from User u join u.orders o join o.items i join i.shop s " +
                        "where o.status=:status and s.user=:user", User.class);
        query.setParameter("status", status).setParameter("user", user);
        return query.getResultList();
    }

    @Override
    public List<User> findAllBuyersForShop(Shop shop) {
        return entityManager.createQuery("select distinct u from User u left join u.orders o " +
                        "left join o.items i where i.shop = ?1 and o.status = ?2", User.class)
                .setParameter(1, shop)
                .setParameter(2, Status.COMPLETE)
                .getResultList();
    }

    @Override
    public Optional<User> findByCouponId(long id) {
        return Optional.ofNullable(QueryResultWrapper.wrapGetSingleResult(
                entityManager.createQuery("select u from User u left join u.coupons coupons " +
                        "where coupons.id = ?1", User.class)
                        .setParameter(1, id)));
    }

}
