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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BirthProvinceImpl implements IBirthProvince {

    public List<BirthProvince> getBirthProvince() {

        String sql = """
            SELECT id_nacimiento, anio, id_provincia, cantidad
            FROM nacimientos_provincias
            ORDER BY id_nacimiento""";

        DatabaseConfig config = new DatabaseConfig();
        DatabaseConnection connection = new DatabaseConnection(config);

        IProvince provinceDao = new ProvinceImpl();
        List<BirthProvince> birthProvinces = new ArrayList<>();
        List<Province> provinces = provinceDao.getProvinces();

        try(Connection connectionProvince = connection.getConnection();
            Statement statement = connectionProvince.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){


            Map<String, Province> provincesMap = new HashMap<>();

            for (Province prov : provinces) {
                if (prov != null && prov.getIdProvince() != null) {
                    provincesMap.put(prov.getIdProvince(), prov);
                }

            }


            while (resultSet.next()) {
                int idBirth = resultSet.getInt("id_nacimiento");
                String idProvince = resultSet.getString("id_provincia");
                int year = resultSet.getInt("anio");
                int cantidad = resultSet.getInt("cantidad");

                Province provinceDb = null;
                if (idProvince != null) {
                    provinceDb = provincesMap.get(idProvince);

                }

                BirthProvince birthProvince = new BirthProvince(idBirth, year, provinceDb, cantidad);
                birthProvinces.add(birthProvince);
            }

            int i = 0;
            while (i < birthProvinces.size()) {
                System.out.print(birthProvinces.get(i).getIdBirth());
                System.out.print(" " + birthProvinces.get(i).getProvince().getIdProvince());
                System.out.print(" " + birthProvinces.get(i).getProvince().getNameProvince());
                System.out.print(" " + birthProvinces.get(i).getYear());
                System.out.println(" " + birthProvinces.get(i).getQuantity());
                i++;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return birthProvinces;
    }

}
