package com.app.models.dao.interfaces;

import com.app.models.entities.BirthRegistration;
import java.util.List;

public interface IBirthRegistration {
    BirthRegistration create(BirthRegistration birthRegistration);

    List<BirthRegistration> findByYear(int year);

    List<BirthRegistration> findAll();
}
