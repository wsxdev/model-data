package com.app.models.dao.interfaces;

import com.app.models.entities.BirthProvince;

import java.util.List;

public interface IBirthProvince {
    public List<BirthProvince> getBirthProvinces();

    public void saveOrUpdate(BirthProvince birthProvince);

    public void deleteByYear(int year);
}
