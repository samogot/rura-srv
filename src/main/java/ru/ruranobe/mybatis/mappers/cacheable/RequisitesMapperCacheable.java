package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.entities.tables.Requisite;
import ru.ruranobe.mybatis.mappers.RequisitesMapper;

import java.util.List;

public class RequisitesMapperCacheable implements RequisitesMapper
{
    private RequisitesMapper mapper;

    public RequisitesMapperCacheable(RequisitesMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public Requisite getRequisiteById(int requisiteId)
    {
        return mapper.getRequisiteById(requisiteId);
    }

    @Override
    public List<Requisite> getAllRequisites()
    {
        return mapper.getAllRequisites();
    }

    @Override
    public void insertRequisite(Requisite requisite)
    {
        mapper.insertRequisite(requisite);
    }

    @Override
    public void deleteRequisite(int requisiteId)
    {
        mapper.deleteRequisite(requisiteId);
    }

    @Override
    public void updateRequisite(Requisite requisite)
    {
        mapper.updateRequisite(requisite);
    }

}
