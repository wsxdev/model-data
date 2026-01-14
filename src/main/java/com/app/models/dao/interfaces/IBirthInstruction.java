package com.app.models.dao.interfaces;

import com.app.models.entities.BirthInstruction;

import java.util.List;

public interface IBirthInstruction {
    public List<BirthInstruction> getBirthInstruction();

    public void saveOrUpdate(BirthInstruction birthInstruction);

    public void deleteByYear(int year);
}
