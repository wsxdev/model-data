package com.app.models.dao.implementations;

import com.app.models.dao.interfaces.IProvince;
import com.app.models.database.DatabaseConnection;
import com.app.models.entities.Province;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProvinceImpl implements IProvince {

    public List<Province> getProvinces() {
        String sql = """
                SELECT id_provincia, provincia
                FROM provincias
                ORDER BY provincia""";

        DatabaseConnection connection = DatabaseConnection.getInstance();
        List<Province> provinces = new ArrayList<>();

        try (Connection connectionProvince = connection.getConnection();
                Statement statement = connectionProvince.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                String idProvince = resultSet.getString("id_provincia");
                String nameProvince = resultSet.getString("provincia");
                Province province = new Province(idProvince, nameProvince);
                provinces.add(province);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return provinces;
    }
}
