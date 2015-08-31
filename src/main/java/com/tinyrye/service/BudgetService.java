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
import com.tinyrye.model.CustomRecurrenceSchedule;
import com.tinyrye.model.FixedRecurrenceInterval;
import com.tinyrye.model.Recurrence;

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
    
    public Recurrence getRecurrenceById(Integer id) {
        return serviceExchange.get(BudgetDao.class).getRecurrenceById(id);
    }

    public Recurrence getOrCreate(Recurrence recurrence)
    {
        if (recurrence.id == null)
        {
            if (recurrence.method == null) {
                throw new IllegalArgumentException("Missing recurrence strategy.");
            }
            else if (recurrence.method instanceof FixedRecurrenceInterval) {
                Recurrence existingRecurrence = serviceExchange.get(BudgetDao.class).findRecurrenceByFixedInterval(recurrence);
                if (existingRecurrence != null) recurrence.id = existingRecurrence.id;
                else serviceExchange.get(BudgetDao.class).insert(recurrence);
            }
            else if (recurrence.method instanceof CustomRecurrenceSchedule) {
                serviceExchange.get(BudgetDao.class).insert(recurrence);
            }
            return recurrence;
        }
        else {
            return serviceExchange.get(BudgetDao.class).getRecurrenceById(recurrence.id);
        }
    }
}