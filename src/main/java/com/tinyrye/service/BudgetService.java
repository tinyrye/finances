package com.tinyrye.service;

import java.time.OffsetDateTime;
import java.util.List;
import javax.sql.DataSource;

import com.tinyrye.dao.AccountsDao;
import com.tinyrye.dao.BudgetDao;
import com.tinyrye.model.AccountHolder;
import com.tinyrye.model.Budget;
import com.tinyrye.model.BudgetItem;
import com.tinyrye.model.EntityId;
import com.tinyrye.model.CustomOccurrenceSchedule;
import com.tinyrye.model.FixedOccurrenceInterval;
import com.tinyrye.model.OccurrenceSchedule;

public class BudgetService
{
    private final ServiceExchange serviceExchange;
    
    public BudgetService(ServiceExchange serviceExchange) {
        this.serviceExchange = serviceExchange;
    }
    
    public HolderBudgetManager managerForAccountHolder(Integer holderId) {
        return managerForAccountHolder(EntityId.of(holderId, AccountHolder.class));
    }
    
    public HolderBudgetManager managerForAccountHolder(EntityId holderReference) {
        return new HolderBudgetManager(serviceExchange, holderReference);
    }
    
    public OccurrenceSchedule getOccurrenceScheduleById(Integer id) {
        return serviceExchange.get(BudgetDao.class).getOccurrenceScheduleById(id);
    }
    
    public OccurrenceSchedule getOrCreate(OccurrenceSchedule occurrence)
    {
        if (occurrence instanceof FixedOccurrenceInterval) {
            return getOrCreate((FixedOccurrenceInterval) occurrence);
        }
        else if (occurrence instanceof CustomOccurrenceSchedule) {
            return getOrCreate((CustomOccurrenceSchedule) occurrence);
        }
        else {
            throw new UnsupportedOperationException("Unsupported OccurrenceSchedule type");
        }
    }
    
    protected OccurrenceSchedule getOrCreate(FixedOccurrenceInterval occurrence)
    {
        if (occurrence.id != null) {
            return serviceExchange.get(BudgetDao.class).getOccurrenceScheduleById(occurrence.id);
        }
        else
        {
            FixedOccurrenceInterval existingOccurrence = serviceExchange.get(BudgetDao.class)
                .findFixedOccurrenceInterval(occurrence);
            if (existingOccurrence != null) {
                occurrence.id = existingOccurrence.id;
            }
            else {
                serviceExchange.get(BudgetDao.class).insert(occurrence);
            }
            return occurrence;
        }
    }
    
    protected OccurrenceSchedule getOrCreate(CustomOccurrenceSchedule occurrence)
    {
        if (occurrence.id != null) {
            return serviceExchange.get(BudgetDao.class).getOccurrenceScheduleById(occurrence.id);
        }
        else {
            serviceExchange.get(BudgetDao.class).insert(occurrence);
            return occurrence;
        }
    }
}