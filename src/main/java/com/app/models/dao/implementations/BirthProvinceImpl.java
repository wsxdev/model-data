package com.app.models.dao.implementations;

import com.app.models.dao.interfaces.IBirthProvince;
import com.app.models.dao.interfaces.IProvince;
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
    public List<BirthProvince> getBirthProvinces() {

        String sql = """
                SELECT id_nacimiento, anio, id_provincia, cantidad
                FROM nacimientos_provincias
                ORDER BY id_nacimiento""";
        // CONEXIÓN A LA BASE DE DATOS
        // CONEXIÓN A LA BASE DE DATOS
        DatabaseConnection connection = DatabaseConnection.getInstance();

        IProvince provinceDao = new ProvinceImpl();
        List<BirthProvince> birthProvinces = new ArrayList<>();
        List<Province> provinces = provinceDao.getProvinces();

        try (Connection connectionProvince = connection.getConnection();
                Statement statement = connectionProvince.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return birthProvinces;
    }

    @Override
    public void saveOrUpdate(BirthProvince birthProvince) {
        String queryCheck = "SELECT id_nacimiento FROM nacimientos_provincias WHERE anio = ? AND id_provincia = ?";
        String queryUpdate = "UPDATE nacimientos_provincias SET cantidad = ? WHERE id_nacimiento = ?";
        String queryInsert = "INSERT INTO nacimientos_provincias (anio, id_provincia, cantidad) VALUES (?, ?, ?)";

        DatabaseConnection connection = DatabaseConnection.getInstance();

        try (Connection conn = connection.getConnection();
                java.sql.PreparedStatement stmtCheck = conn.prepareStatement(queryCheck)) {

            stmtCheck.setInt(1, birthProvince.getYear());
            stmtCheck.setString(2, birthProvince.getProvince().getIdProvince());

            try (ResultSet rs = stmtCheck.executeQuery()) {
                if (rs.next()) {
                    // Update
                    int id = rs.getInt("id_nacimiento");
                    try (java.sql.PreparedStatement stmtUpdate = conn.prepareStatement(queryUpdate)) {
                        stmtUpdate.setInt(1, birthProvince.getQuantity());
                        stmtUpdate.setInt(2, id);
                        stmtUpdate.executeUpdate();
                    }
                } else {
                    // Insert
                    try (java.sql.PreparedStatement stmtInsert = conn.prepareStatement(queryInsert)) {
                        stmtInsert.setInt(1, birthProvince.getYear());
                        stmtInsert.setString(2, birthProvince.getProvince().getIdProvince());
                        stmtInsert.setInt(3, birthProvince.getQuantity());
                        stmtInsert.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving/updating birth province: " + e.getMessage(), e);
        }
    }
}
