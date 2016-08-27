package com.softwhistle.service;

import java.time.OffsetDateTime;
import java.util.List;
import javax.sql.DataSource;

import com.softwhistle.dao.AccountsDao;
import com.softwhistle.dao.BudgetDao;
import com.softwhistle.model.AccountHolder;
import com.softwhistle.model.Budget;
import com.softwhistle.model.BudgetItem;
import com.softwhistle.model.EntityId;
import com.softwhistle.model.CustomOccurrenceSchedule;
import com.softwhistle.model.FixedFieldOccurrences;
import com.softwhistle.model.FixedUnitOccurrences;
import com.softwhistle.model.OccurrenceSchedule;

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
        if (occurrence instanceof FixedUnitOccurrences) {
            return getOrCreate((FixedUnitOccurrences) occurrence);
        }
        if (occurrence instanceof FixedFieldOccurrences) {
            return getOrCreate((FixedFieldOccurrences) occurrence);
        }
        else if (occurrence instanceof CustomOccurrenceSchedule) {
            return getOrCreate((CustomOccurrenceSchedule) occurrence);
        }
        else {
            throw new UnsupportedOperationException("Unsupported OccurrenceSchedule type");
        }
    }
    
    protected OccurrenceSchedule getOrCreate(FixedUnitOccurrences occurrence)
    {
        if (occurrence.id != null) {
            return serviceExchange.get(BudgetDao.class)
                .getOccurrenceScheduleById(occurrence.id);
        }
        else
        {
            FixedUnitOccurrences existingOccurrence = serviceExchange.get(BudgetDao.class)
                .findFixedUnitOccurrences(occurrence);
            if (existingOccurrence != null) {
                occurrence.id = existingOccurrence.id;
            }
            else {
                serviceExchange.get(BudgetDao.class).insert(occurrence);
            }
            return occurrence;
        }
    }
    
    protected OccurrenceSchedule getOrCreate(FixedFieldOccurrences occurrence)
    {
        if (occurrence.id != null) {
            return serviceExchange.get(BudgetDao.class)
                .getOccurrenceScheduleById(occurrence.id);
        }
        else
        {
            FixedFieldOccurrences existingOccurrence = serviceExchange.get(BudgetDao.class)
                .findFixedFieldOccurrences(occurrence);
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
