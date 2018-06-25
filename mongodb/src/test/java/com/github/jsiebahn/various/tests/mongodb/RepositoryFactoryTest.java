package com.github.jsiebahn.various.tests.mongodb;

import com.mongodb.MongoClient;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.repository.CrudRepository;

import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 15.11.16 09:02
 */
public class RepositoryFactoryTest {

    @Test
    @Ignore("MongoDB required at localhost")
    public void shouldInstantiateRepository() throws UnknownHostException {
        MongoOperations mongoOperations = new MongoTemplate(new MongoClient("localhost"), "myMongoDb");
        MongoRepositoryFactory factory = new MongoRepositoryFactory(mongoOperations);
        MyRepo repository = factory.getRepository(MyRepo.class);

        MyEntity e = new MyEntity();
        e.setName("Foo");

        repository.save(e);

        Iterable<MyEntity> myEntities = repository.findAll();
        AtomicInteger i = new AtomicInteger(0);
        myEntities.forEach(myEntity -> {
            i.incrementAndGet();
            Assert.assertNotNull(e.getId());
            Assert.assertEquals("Foo", e.getName());
            repository.delete(myEntity);
        });
        Assert.assertEquals(1, i.get());
    }

    class MyEntity {
        String id;

        String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    interface MyRepo extends CrudRepository<MyEntity, String> {

    }

}
