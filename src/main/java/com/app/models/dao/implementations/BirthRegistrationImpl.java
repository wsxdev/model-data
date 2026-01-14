package com.app.models.dao.implementations;

import com.app.models.dao.interfaces.IBirthRegistration;
import com.app.models.database.DatabaseConnection;
import com.app.models.entities.BirthRegistration;
import com.app.models.entities.Instruction;
import com.app.models.entities.Mother;
import com.app.models.entities.Province;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

public class BirthRegistrationImpl implements IBirthRegistration {

    private final DatabaseConnection databaseConnection;

    public BirthRegistrationImpl() {
        this.databaseConnection = DatabaseConnection.getInstance();
    }

    @Override
    public BirthRegistration create(BirthRegistration birthRegistration) {
        String sql = """
                INSERT INTO nacimiento (id_madre, id_provincia, id_instruccion, fecha_nacimiento, anio, sexo, tipo_parto)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id_nacimiento
                """;

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, birthRegistration.getMother().getIdMother());
            stmt.setString(2, birthRegistration.getProvince().getIdProvince());
            stmt.setString(3, birthRegistration.getInstruction().getIdInstruction());
            stmt.setDate(4, birthRegistration.getBirthDate());
            stmt.setInt(5, birthRegistration.getYear());
            stmt.setString(6, birthRegistration.getSex());
            stmt.setString(7, birthRegistration.getBirthType());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    birthRegistration.setIdBirthRegistration(rs.getInt("id_nacimiento"));
                    return birthRegistration;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error registering birth: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<BirthRegistration> findByYear(int year) {
        List<BirthRegistration> list = new ArrayList<>();
        String sql = """
                SELECT n.id_nacimiento, n.fecha_nacimiento, n.anio, n.sexo, n.tipo_parto,
                       m.id_madre, m.identificacion, m.nombres, m.edad, m.estado_civil,
                       p.id_provincia, p.provincia,
                       i.id_instruccion, i.instruccion
                FROM nacimiento n
                JOIN madre m ON n.id_madre = m.id_madre
                JOIN provincias p ON n.id_provincia = p.id_provincia
                JOIN instrucciones i ON n.id_instruccion = i.id_instruccion
                WHERE n.anio = ?
                """;

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, year);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Mother m = new Mother(
                            rs.getInt("id_madre"),
                            rs.getString("identificacion"),
                            rs.getString("nombres"),
                            rs.getInt("edad"),
                            rs.getString("estado_civil"));
                    Province p = new Province(rs.getString("id_provincia"), rs.getString("provincia"));
                    Instruction i = new Instruction(rs.getString("id_instruccion"), rs.getString("instruccion"));

                    BirthRegistration br = new BirthRegistration(
                            rs.getInt("id_nacimiento"),
                            m, p, i,
                            rs.getDate("fecha_nacimiento"),
                            rs.getString("sexo"),
                            rs.getString("tipo_parto"));
                    list.add(br);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding births by year: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<BirthRegistration> findAll() {
        List<BirthRegistration> list = new ArrayList<>();
        String sql = """
                SELECT n.id_nacimiento, n.fecha_nacimiento, n.anio, n.sexo, n.tipo_parto,
                       m.id_madre, m.identificacion, m.nombres, m.edad, m.estado_civil,
                       p.id_provincia, p.provincia,
                       i.id_instruccion, i.instruccion
                FROM nacimiento n
                JOIN madre m ON n.id_madre = m.id_madre
                JOIN provincias p ON n.id_provincia = p.id_provincia
                JOIN instrucciones i ON n.id_instruccion = i.id_instruccion
                ORDER BY n.id_nacimiento DESC
                """;

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Mother m = new Mother(
                        rs.getInt("id_madre"),
                        rs.getString("identificacion"),
                        rs.getString("nombres"),
                        rs.getInt("edad"),
                        rs.getString("estado_civil"));
                Province p = new Province(rs.getString("id_provincia"), rs.getString("provincia"));
                Instruction i = new Instruction(rs.getString("id_instruccion"), rs.getString("instruccion"));

                BirthRegistration br = new BirthRegistration(
                        rs.getInt("id_nacimiento"),
                        m, p, i,
                        rs.getDate("fecha_nacimiento"),
                        rs.getString("sexo"),
                        rs.getString("tipo_parto"));
                list.add(br);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding all births: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(BirthRegistration birthRegistration) {
        String sql = """
                UPDATE nacimiento
                SET id_madre = ?, id_provincia = ?, id_instruccion = ?, fecha_nacimiento = ?, anio = ?, sexo = ?, tipo_parto = ?
                WHERE id_nacimiento = ?
                """;

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, birthRegistration.getMother().getIdMother());
            stmt.setString(2, birthRegistration.getProvince().getIdProvince());
            stmt.setString(3, birthRegistration.getInstruction().getIdInstruction());
            stmt.setDate(4, birthRegistration.getBirthDate());
            stmt.setInt(5, birthRegistration.getYear());
            stmt.setString(6, birthRegistration.getSex());
            stmt.setString(7, birthRegistration.getBirthType());
            stmt.setInt(8, birthRegistration.getIdBirthRegistration());

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error updating birth registration: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM nacimiento WHERE id_nacimiento = ?";
        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting birth registration: " + e.getMessage(), e);
        }
    }
}
