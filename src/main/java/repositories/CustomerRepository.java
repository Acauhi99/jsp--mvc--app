package repositories;

import models.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

public class CustomerRepository extends BaseRepository<Customer> {
    
    public CustomerRepository() {
        super(Customer.class);
    }
    
    public Optional<Customer> findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE c.email = :email", Customer.class);
            query.setParameter("email", email);
            try {
                Customer customer = query.getSingleResult();
                return Optional.of(customer);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        } finally {
            em.close();
        }
    }
    
}
