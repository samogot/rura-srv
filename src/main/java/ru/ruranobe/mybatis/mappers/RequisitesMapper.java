package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.Requisite;

import java.util.List;

public interface RequisitesMapper
{
    Requisite getRequisiteById(int requisiteId);

    List<Requisite> getAllRequisites();

    void insertRequisite(Requisite requisite);

    void deleteRequisite(int requisiteId);

    void updateRequisite(Requisite requisite);
}
