package com.eleuterio.abarrotes.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class StockRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public boolean descontarStockFefo(Integer productoId, Integer cantidad) {
        Boolean result = (Boolean) entityManager
                .createNativeQuery("SELECT descontar_stock_fefo(:productoId, :cantidad)")
                .setParameter("productoId", productoId)
                .setParameter("cantidad", cantidad)
                .getSingleResult();
        return Boolean.TRUE.equals(result);
    }
}
