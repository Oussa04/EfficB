package org.meteor.efficaisse.repository;

import org.meteor.efficaisse.model.Payment;
import org.meteor.efficaisse.model.Store;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment,Payment.PaymentId> {
    Payment findByStoreAndId_Id(Store store, Integer id);
}
