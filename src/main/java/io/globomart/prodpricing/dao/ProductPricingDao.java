package io.globomart.prodpricing.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.globomart.prodpricing.dto.Pricing;
import io.globomart.prodpricing.entities.PricingEntity;

public abstract class ProductPricingDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductPricingDao.class);

	public static PricingEntity createPricing(Pricing pricing) {
		EntityManager theManager = PersistanceUtil.createEntityManager();
		theManager.getTransaction().begin();
		PricingEntity pricingEntity = new PricingEntity(pricing.getProductId(), pricing.getSupplierId(),pricing.getPrice());
		theManager.persist(pricingEntity);
		theManager.getTransaction().commit();
		LOGGER.info("Pricing created successfully for " + pricingEntity);
		return pricingEntity;
	}

	public static List<PricingEntity> getPricing(Map<String, String> filter) {
		EntityManager entityManager = PersistanceUtil.createEntityManager();
		List<PricingEntity> result = entityManager.createQuery(queryBuilder(entityManager, filter)).getResultList();
		return result;
	}

	public static PricingEntity getPricingById(int pricingId) {
		EntityManager entityManager = PersistanceUtil.createEntityManager();
		return entityManager.find(PricingEntity.class, pricingId);
	}

	private static CriteriaQuery<PricingEntity> queryBuilder(EntityManager entityManager,
			Map<String, String> searchCriteria) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PricingEntity> criteriaQuery = criteriaBuilder.createQuery(PricingEntity.class);
		Root<PricingEntity> pricings = criteriaQuery.from(PricingEntity.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		if (searchCriteria != null) {
			for (String searchKey : searchCriteria.keySet()) {
				predicates.add(criteriaBuilder.equal(pricings.get(searchKey), searchCriteria.get(searchKey)));
			}
		}
		CriteriaQuery<PricingEntity> query = criteriaQuery.select(pricings)
				.where(predicates.toArray(new Predicate[] {}));
		return query;

	}
}
