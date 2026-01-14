package com.app.models.dao.interfaces;

import com.app.models.entities.Mother;

public interface IMother {
    Mother create(Mother mother);

    Mother findByIdentification(String identification);
}
