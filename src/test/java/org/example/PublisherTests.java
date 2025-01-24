package org.example;

import dao.PublisherDAO;
import entity.PublishersEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PublisherTests {

    private EntityManagerFactory emf;
    private EntityManager entityManager;
    private PublisherDAO publisherDAO;

    @BeforeAll
    void setUp() {
        emf = Persistence.createEntityManagerFactory("LibraryPersistenceUnit");
        entityManager = emf.createEntityManager();
        publisherDAO = new PublisherDAO(entityManager);
    }

    @Test
    void testCreatePublisher() {
        PublishersEntity publisher = new PublishersEntity("Publisher Name", "Publisher Address", "123456789");
        publisherDAO.create(publisher);

        PublishersEntity fetchedPublisher = entityManager.find(PublishersEntity.class, publisher.getId());

        assertNotNull(fetchedPublisher);
        assertEquals(publisher.getName(), fetchedPublisher.getName());
        assertEquals(publisher.getAddress(), fetchedPublisher.getAddress());
        assertEquals(publisher.getPhonenumber(), fetchedPublisher.getPhonenumber());
    }

    @Test
    void testGetPublisherById() {
        PublishersEntity publisher = new PublishersEntity("Publisher Name", "Publisher Address", "987654321");
        publisherDAO.create(publisher);

        PublishersEntity fetchedPublisher = publisherDAO.getById(publisher.getId());

        assertNotNull(fetchedPublisher);
        assertEquals(publisher.getId(), fetchedPublisher.getId());
        assertEquals("Publisher Name", fetchedPublisher.getName());
    }

    @Test
    void testUpdatePublisher() {
        PublishersEntity publisher = new PublishersEntity("Old Publisher", "Old Address", "111222333");
        publisherDAO.create(publisher);

        publisher.setName("Updated Publisher");
        publisher.setAddress("Updated Address");
        publisher.setPhonenumber("444555666");
        publisherDAO.update(publisher);

        PublishersEntity updatedPublisher = entityManager.find(PublishersEntity.class, publisher.getId());

        assertNotNull(updatedPublisher);
        assertEquals("Updated Publisher", updatedPublisher.getName());
        assertEquals("Updated Address", updatedPublisher.getAddress());
        assertEquals("444555666", updatedPublisher.getPhonenumber());
    }

    @Test
    void testDeletePublisher() {
        PublishersEntity publisher = new PublishersEntity("Delete Me Publisher", "Delete Me Address", "777888999");
        publisherDAO.create(publisher);

        publisherDAO.delete(publisher.getId());

        PublishersEntity deletedPublisher = entityManager.find(PublishersEntity.class, publisher.getId());
        assertNull(deletedPublisher);
    }
}
