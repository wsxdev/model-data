package com.app.models.dao.implementations;

import com.app.models.dao.interfaces.IBirthProvince;
import com.app.models.dao.interfaces.IProvince;
import com.app.models.database.DatabaseConfig;
import com.app.models.database.DatabaseConnection;
import com.app.models.entities.BirthProvince;
import com.app.models.entities.Province;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BirthProvinceImpl implements IBirthProvince {

    public List<BirthProvince> getBirthProvince() {

        String sql = """
            SELECT id_nacimiento, anio, id_provincia, cantidad
            FROM nacimientos_provincias
            ORDER BY id_nacimiento""";

        DatabaseConfig config = new DatabaseConfig();
        DatabaseConnection connection = new DatabaseConnection(config);

        try(Connection connectionProvince = connection.getConnection();
            Statement statement = connectionProvince.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){

            List<BirthProvince> birthProvinces = new ArrayList<>();
            IProvince province = new ProvinceImpl();
            List<Province> provinces = province.getProvinces();


            while (resultSet.next()) {
                int idBirth = resultSet.getInt("id_nacimiento");
                String idProvince = resultSet.getString("id_provincia");
                int year = resultSet.getInt("anio");
                int cantidad = resultSet.getInt("cantidad");


                BirthProvince birthProvince = new BirthProvince( idBirth, idProvince, year, cantidad);
                birthProvince.add(birthProvince);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
