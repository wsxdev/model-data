package com.app.models.dao.implementations;

import com.app.models.dao.interfaces.IProvince;
import com.app.models.database.DatabaseConfig;
import com.app.models.database.DatabaseConnection;
import com.app.models.entities.Province;

import java.sql.*;
import java.util.ArrayList;

public class ProvinceImpl implements IProvince {

    private ArrayList<Province> province = new ArrayList<>();
    public ArrayList<Province> getProvinces() {
        String sql =
        """
        SELECT id_provincia, provincia
        FROM provincias
        ORDER BY provincias""";

        DatabaseConfig config = new DatabaseConfig();
        DatabaseConnection connection = new DatabaseConnection(config);

        try (Connection test = connection.getConnection();
             Statement statement = test.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            // test.getConnection().prepareStatement(sql).executeQuery();
            while (resultSet.next()) {
                int id_provincia = resultSet.getInt("id_provincia");
                String provincia = resultSet.getString("provincia");
                System.out.println("ID: " + id_provincia + "    Provincia: " + provincia);
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return province;
    }
}
