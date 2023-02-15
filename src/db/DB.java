package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB {
	private Connection conn;

	private static DB instance;

	public static Reserva guardarReserva(Reserva reserva) {
		DB db = new DB();
		List<Reserva> list = new ArrayList<>();
		try (PreparedStatement pstmt = db.conn.prepareStatement("INSERT INTO reservas\r\n"
				+ "( fecha_entrada, fecha_salida, valor, forma_pago)\r\n" + "VALUES( ?, ?,?, ?);\r\n" , Statement.RETURN_GENERATED_KEYS);) {
			pstmt.setTimestamp(1, reserva.getFechaEntrada());
			pstmt.setTimestamp(2, reserva.getFechaSalida());
			pstmt.setDouble(3, reserva.getValor());
			pstmt.setString(4, reserva.getFormaPago());
			int rs = pstmt.executeUpdate();
			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				reserva.setId(generatedKeys.getInt(1));
			       System.out.println("Reserva Generada "+reserva.getId());
			}

		} catch (Exception e) {
			System.out.println(e);
		}

		return reserva;
	}

	public static User obtenerPorUsernameYPassword(String username, String password) throws Exception {
		DB db = new DB();
		User user = null;
		try (PreparedStatement pstmt = db.conn
				.prepareStatement("SELECT * FROM usuarios where nombre_usuario=? and password=?");) {
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			List<User> users = new ArrayList<User>();
			if (rs.next()) {
				user = new User();
				user.setId(rs.getInt("id"));
				user.setUsername(rs.getString("nombre_usuario"));
				user.setPassword(rs.getString("password"));

			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return user;
	}

	public static List<Huesped> buscarHuespedes(String textBusqueda) throws Exception {
		DB db = new DB();
		List<Huesped> list = new ArrayList<Huesped>();
		try (PreparedStatement pstmt = db.conn
				.prepareStatement("SELECT id, nombre, apellido, fecha_nacimiento, nacionalidad, telefono, id_reserva "
						+ "FROM huespedes" + "");) {
			// pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Huesped huesped = new Huesped(rs.getInt("id"), rs.getString("nombre"), rs.getString("apellido"),
						rs.getTimestamp("fecha_nacimiento"), rs.getString("nacionalidad"), rs.getString("telefono"),
						rs.getInt("id_reserva"));
				list.add(huesped);

			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return list;
	}

	public static List<Reserva> buscarReserva(String textBusqueda) throws Exception {
		DB db = new DB();
		List<Reserva> list = new ArrayList<>();
		try (PreparedStatement pstmt = db.conn.prepareStatement(
				"SELECT id, fecha_entrada, fecha_salida, valor, forma_pago " + "FROM reservas" + "" + "");) {
			// pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Reserva huesped = new Reserva(rs.getInt("id"), rs.getTimestamp("fecha_entrada"),
						rs.getTimestamp("fecha_salida"), rs.getDouble("valor"), rs.getString("forma_pago"));
				list.add(huesped);

			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return list;
	}

	public static void init() {
		instance = new DB();
	}

	private DB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_alura?serverTimezone=UTC", "root",
					"root");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private ResultSet executeQuery(String query) {
		try {
			Statement stmt = conn.createStatement();
			return stmt.executeQuery(query);
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	private Statement createStatement() throws SQLException {
		return conn.createStatement();
	}

	private void close() {
		try {
			conn.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String[] args) {
		DB db = new DB();
		ResultSet rs = db.executeQuery("SELECT * FROM usuario");
		List<User> users = new ArrayList<User>();
		try {
			while (rs.next()) {
				User user = new User();
				user.setId(rs.getInt("id"));
				user.setUsername(rs.getString("nombre_usuario"));
				user.setPassword(rs.getString("password"));
				users.add(user);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		db.close();

		for (User user : users) {
			System.out.println("ID: " + user.getId() + ", Nombre de usuario: " + user.getUsername() + ", Contraseña: "
					+ user.getPassword());
		}
	}

	public static void guardarHuesped(Huesped huesped) {
		DB db = new DB();
		
		try (PreparedStatement pstmt = db.conn.prepareStatement("INSERT INTO hotel_alura.huespedes\r\n" + 
				"(nombre, apellido, fecha_nacimiento, nacionalidad, telefono, id_reserva)\r\n" + 
				"VALUES(?, ?, ?, ?, ?, ?);\r\n"  , Statement.RETURN_GENERATED_KEYS);) {
			pstmt.setString(1, huesped.getNombre());
			pstmt.setString(2, huesped.getApellido());
			pstmt.setTimestamp(3, huesped.getFechaNacimiento());
			pstmt.setString(4, huesped.getNacionalidad());
			pstmt.setString(5, huesped.getTelefono() );
			pstmt.setInt(6, huesped.getIdReserva());
			int rs = pstmt.executeUpdate();
			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				huesped.setId(generatedKeys.getInt(1));
			       System.out.println("Reserva Generada "+huesped.getId());
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
}
