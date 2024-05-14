package software.crud.MySQL;

import java.sql.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import software.crud.Models.*;
import software.crud.Utility.Helper;

public class MySQLDBHelper {

	private Map<String, List<ColumnModel>> dicColumn = new HashMap<>();
	private Map<String, List<PrimaryKeyClass>> dicPrimaryKey = new HashMap<>();

	private String connectionString;
	private Connection dbCon;
	private String host;
	private int port;
	private String username;
	private String password;
	private String dbName;

	public MySQLDBHelper(String host, String port, String username, String password, String dbName) {
		this.host = host;
		this.port = Integer.parseInt(port);
		this.username = username;
		this.password = password;
		this.dbName = dbName;
		this.connectionString = String.format(
				"jdbc:mysql://%s:%d/%s?useSSL=false&characterEncoding=utf8&allowPublicKeyRetrieval=true", host,
				this.port, dbName);
	}

	public boolean connect() throws SQLException {
		dbCon = DriverManager.getConnection(connectionString, username, password);
		return dbCon != null;
	}

	private boolean isConnected() {
		try {
			if (dbCon == null || dbCon.isClosed()) {
				dbCon = DriverManager.getConnection(connectionString, username, password);
			}
			return dbCon != null && !dbCon.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Getters and Setters

	public Map<String, List<ColumnModel>> getDicColumn() {
		return dicColumn;
	}

	public void setDicColumn(Map<String, List<ColumnModel>> dicColumn) {
		this.dicColumn = dicColumn;
	}

	public Map<String, List<PrimaryKeyClass>> getDicPrimaryKey() {
		return dicPrimaryKey;
	}

	public void setDicPrimaryKey(Map<String, List<PrimaryKeyClass>> dicPrimaryKey) {
		this.dicPrimaryKey = dicPrimaryKey;
	}

	public String getConnectionString() {
		return connectionString;
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

	public Connection getDbCon() {
		return dbCon;
	}

	public void setDbCon(Connection dbCon) {
		this.dbCon = dbCon;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDBName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public List<String> getListOfTables() throws SQLException {
		List<String> tables = new ArrayList<>();
		if (isConnected()) {
			try (Statement statement = dbCon.createStatement();
					ResultSet resultSet = statement.executeQuery(
							"SELECT table_name FROM information_schema.tables WHERE TABLE_TYPE NOT LIKE 'VIEW' AND table_schema = '"
									+ dbName + "'")) {
				while (resultSet.next()) {
					tables.add(resultSet.getString(1));
				}
			}
		}
		return tables;
	}

	public List<String> getListOfViews() throws SQLException {
		List<String> views = new ArrayList<>();
		if (isConnected()) {
			try (Statement statement = dbCon.createStatement();
					ResultSet resultSet = statement.executeQuery(
							"SELECT table_name FROM information_schema.tables WHERE TABLE_TYPE LIKE 'VIEW' AND table_schema = '"
									+ dbName + "'")) {
				while (resultSet.next()) {
					views.add(resultSet.getString(1));
				}
			}
		}
		return views;
	}

	public List<ColumnModel> getTableColumns(String tableName) throws SQLException {
		List<ColumnModel> columns = new ArrayList<>();

		if (tableName == null || tableName.isEmpty()) {
			System.out.println("Error: Provided tableName is null or empty.");
			return columns;
		}

		if (dbName == null || dbName.isEmpty()) {
			System.out.println("Error: Database name (dbName) is null or empty.");
			return columns;
		}

		if (isConnected()) {
			try (Statement statement = dbCon.createStatement();
					ResultSet resultSet = statement.executeQuery("SHOW COLUMNS FROM `" + dbName + "`." + tableName)) {
				while (resultSet.next()) {
					ColumnModel column = new ColumnModel();
					column.setField(resultSet.getString(1));
					column.setTypeName(resultSet.getString(2));
					column.setIsNull(resultSet.getString(3));
					column.setKey(resultSet.getString(4));
					column.setDefaultValue(resultSet.getString(5));
					column.setExtra(resultSet.getString(6));
					columns.add(column);
				}
			}

			try (Statement statement = dbCon.createStatement();
					ResultSet resultSet = statement.executeQuery(
							"SELECT COLUMN_NAME, CONSTRAINT_NAME, REFERENCED_COLUMN_NAME, REFERENCED_TABLE_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_NAME = '"
									+ tableName + "' AND TABLE_SCHEMA='" + dbName + "'")) {
				while (resultSet.next()) {
					FKDetails fkDetails = new FKDetails();
					fkDetails.setCOLUMN_NAME(resultSet.getString(1));
					fkDetails.setCONSTRAINT_NAME(resultSet.getString(2));
					fkDetails.setREFERENCED_COLUMN_NAME(resultSet.getString(3));
					fkDetails.setREFERENCED_TABLE_NAME(resultSet.getString(4));
					if (fkDetails.getREFERENCED_TABLE_NAME() != null) {
						for (ColumnModel col : columns) {
							if (col.getField().equalsIgnoreCase(fkDetails.getCOLUMN_NAME())) {
								col.setFkDetails(fkDetails);
								break;
							}
						}
					}
				}
			}
		} else {
			System.out.println("Error: Could not establish a connection to the database.");
		}

		return columns;
	}

	public List<PrimaryKeyClass> getPrimaryKey(String tableName) throws SQLException {
		List<PrimaryKeyClass> primaryKeys = new ArrayList<>();

		if (dicPrimaryKey.containsKey(tableName)) {
			return dicPrimaryKey.get(tableName);
		}

		List<ColumnModel> columns = dicColumn.getOrDefault(tableName, getTableColumns(tableName));
		dicColumn.put(tableName, columns);

		List<ColumnModel> pkColumns = columns.stream()
				.filter(column -> "PRI".equals(column.getKey()))
				.collect(Collectors.toList());

		if (!pkColumns.isEmpty()) {
			primaryKeys.addAll(pkColumns.stream()
					.map(column -> new PrimaryKeyClass(column.getTypeName(), column.getField()))
					.collect(Collectors.toList()));
		} else {
			ColumnModel autoIncrementColumn = columns.stream()
					.filter(column -> "auto_increment".equals(column.getExtra()))
					.findFirst()
					.orElse(null);

			if (autoIncrementColumn != null) {
				primaryKeys.add(new PrimaryKeyClass(autoIncrementColumn.getTypeName(), autoIncrementColumn.getField()));
			} else if (!columns.isEmpty()) {
				ColumnModel firstIntColumn = columns.stream()
						.filter(column -> column.getTypeName().toLowerCase().contains("int"))
						.findFirst()
						.orElse(columns.get(0));
				primaryKeys.add(new PrimaryKeyClass(firstIntColumn.getTypeName(), firstIntColumn.getField()));
			}
		}

		dicPrimaryKey.put(tableName, primaryKeys);
		return primaryKeys;
	}

	public SelectQueryData getSelectQueryData(String tableName) throws SQLException {
		SelectQueryData selectQueryData = new SelectQueryData();

		List<PrimaryKeyClass> primaryKeys = dicPrimaryKey.getOrDefault(tableName, getPrimaryKey(tableName));
		dicPrimaryKey.put(tableName, primaryKeys);
		selectQueryData.setPrimaryKeys(primaryKeys);

		List<ColumnModel> columns = dicColumn.getOrDefault(tableName, getTableColumns(tableName));
		dicColumn.put(tableName, columns);
		selectQueryData.setColumnList(columns);

		List<String> selectColumnList = new ArrayList<>();
		List<JoinColumnClass> joinQueryData = new ArrayList<>();
		List<FKColumnClass> fkColumnData = new ArrayList<>();
		List<String> tableCharacters = new ArrayList<>();

		for (ColumnModel column : columns) {
			selectColumnList.add(column.getField());

			if (column.getFkDetails() != null) {
				List<ColumnModel> refTableColumns = getTableColumns(column.getFkDetails().getREFERENCED_TABLE_NAME());
				if (!refTableColumns.isEmpty()) {
					ColumnModel refColumnName = refTableColumns.stream()
							.filter(refColumn -> "NO".equalsIgnoreCase(refColumn.getIsNull())
									&& !"PRI".equals(refColumn.getKey()) && refColumn.getTypeName().contains("char"))
							.findFirst()
							.orElseGet(() -> refTableColumns.stream()
									.filter(refColumn -> !"PRI".equals(refColumn.getKey())
											&& refColumn.getTypeName().contains("char"))
									.findFirst()
									.orElse(refTableColumns.stream()
											.filter(refColumn -> !"PRI".equals(refColumn.getKey()))
											.findFirst()
											.orElse(null)));
					if (refColumnName != null && fkColumnData.stream()
							.noneMatch(fkColumn -> fkColumn.getFieldName2().equals(refColumnName.getField()))) {
						String tableCharacter = Helper.getTableCharacter(tableCharacters,
								column.getFkDetails().getREFERENCED_TABLE_NAME());
						tableCharacters.add(tableCharacter);
						fkColumnData.add(new FKColumnClass(column.getField(), column.getTypeName(),
								column.getFkDetails().getREFERENCED_TABLE_NAME(), refColumnName.getField(), tableName,
								tableCharacter, column.getFkDetails().getREFERENCED_COLUMN_NAME(),
								refColumnName.getTypeName()));
						joinQueryData.add(new JoinColumnClass(column.getFkDetails().getREFERENCED_TABLE_NAME(),
								column.getFkDetails().getREFERENCED_COLUMN_NAME(), tableName, tableCharacter,
								column.getField(), refColumnName, column));
						selectColumnList.add(refColumnName.getField());
					}
				}
			}
		}

		selectQueryData.setFkColumnData(fkColumnData);
		selectQueryData.setJoinQueryData(joinQueryData);
		selectQueryData.setSelectColumnList(selectColumnList);

		return selectQueryData;
	}

	public InsertUpdateQueryData getInsertUpdateQueryData(String tableName) throws SQLException {
		InsertUpdateQueryData insertUpdateQueryData = new InsertUpdateQueryData();

		List<PrimaryKeyClass> primaryKeys = dicPrimaryKey.getOrDefault(tableName, getPrimaryKey(tableName));
		dicPrimaryKey.put(tableName, primaryKeys);
		insertUpdateQueryData.setPrimaryKeys(primaryKeys);

		List<ColumnModel> columns = dicColumn.getOrDefault(tableName, getTableColumns(tableName));
		dicColumn.put(tableName, columns);
		insertUpdateQueryData.setColumnList(columns);

		List<InsertUpdateClass> insertColumnList = new ArrayList<>();
		List<InsertUpdateClass> updateColumnList = new ArrayList<>();

		for (ColumnModel column : columns) {
			if (!"auto_increment".equals(column.getExtra()) && !"CURRENT_TIMESTAMP".equals(column.getDefaultValue())) {
				boolean isRequired = "no".equalsIgnoreCase(column.getIsNull());
				InsertUpdateClass insertUpdateClass = new InsertUpdateClass(column.getTypeName(), column.getField(),
						isRequired, column.getDefaultValue());
				insertColumnList.add(insertUpdateClass);
				updateColumnList.add(insertUpdateClass);
			}
		}

		insertUpdateQueryData.setInsertColumnList(insertColumnList);
		insertUpdateQueryData.setUpdateColumnList(updateColumnList);

		return insertUpdateQueryData;
	}

	public FinalQueryData buildLaravelQuery(String tableName) throws SQLException {
		FinalQueryData finalQueryData = new FinalQueryData();
		SelectQueryData selectQueryData = getSelectQueryData(tableName);
		InsertUpdateQueryData insertUpdateQueryData = getInsertUpdateQueryData(tableName);

		String selectOneQuery = "SELECT {fkColumns} t.* FROM " + tableName
				+ " t {joinQuery} WHERE {selectPrimaryKey} LIMIT 0,1";
		String selectAllQuery = "SELECT {fkColumns} t.* FROM " + tableName + " t {joinQuery} LIMIT ?, ?";
		String searchQuery = "SELECT {fkColumns} t.* FROM " + tableName
				+ " t {joinQuery} WHERE {searchCondition} LIMIT ?,?";
		String searchRecordCountQuery = "SELECT count(*) TotalCount FROM " + tableName
				+ " t {joinQuery} WHERE {searchCondition} ";
		String selectAllRecordCountQuery = "SELECT count(*) TotalCount FROM " + tableName + " t {joinQuery} ";
		String joinQueryString = "";
		String fkColumnString = "";
		String searchConditionString = "";
		String deleteQuery = "DELETE FROM " + tableName + " WHERE {deletePrimaryKey}";
		String primaryKeyControllerString = "";
		String createPropertyListString = "";
		String propertyListString = "";
		String requiredFieldString = "";

		for (ColumnModel column : selectQueryData.getColumnList()) {
			if ("CURRENT_TIMESTAMP".equals(column.getDefaultValue())) {
				propertyListString += "\nthis." + column.getField() + " = new Date();";
				createPropertyListString += "\n" + column.getField() + ": new Date(),";
			} else if ("auto_increment".equals(column.getExtra())) {
				propertyListString += "\nthis." + column.getField() + " = 0;";
				createPropertyListString += "\n" + column.getField() + ":0,";
			} else if (!"auto_increment".equals(column.getExtra())
					&& !"CURRENT_TIMESTAMP".equals(column.getDefaultValue())) {
				propertyListString += "\nthis." + column.getField() + " = " + tableName + "." + column.getField() + ";";
				createPropertyListString += "\n" + column.getField() + ":req.body." + column.getField() + ",";
				if ("NO".equals(column.getIsNull())) {
					requiredFieldString = (requiredFieldString != null && !requiredFieldString.isEmpty())
							? (requiredFieldString + " || !createObj." + column.getField())
							: ("!createObj." + column.getField());
				}
			}
			if (!"auto_increment".equals(column.getExtra())) {
				searchConditionString += " OR LOWER(t." + column.getField()
						+ ") LIKE CONCAT('%','\"+searchKey+\"','%')";
			}
		}
		for (FKColumnClass fkColumn : selectQueryData.getFKColumnData()) {
			fkColumnString += " " + fkColumn.getTableChar2() + "." + fkColumn.getFieldName2() + " as "
					+ fkColumn.getFieldName1() + "_Value,";
			propertyListString += "\nthis." + fkColumn.getFieldName1() + "_Value = " + tableName + "."
					+ fkColumn.getFieldName1() + "_Value;";
		}

		for (JoinColumnClass joinColumn : selectQueryData.getJoinQueryData()) {
			joinQueryString += " join " + joinColumn.getTableName2() + " " + joinColumn.getTableChar2() + " on t."
					+ joinColumn.getFieldName1() + " = " + joinColumn.getTableChar2() + "." + joinColumn.getFieldName2()
					+ " ";
		}

		String selectPrimaryKeyString = "";
		String deletePrimaryKeyString = "";
		String primaryKeyString = "";
		String primaryKeyCommaString = "";

		if (selectQueryData.getPrimaryKeys() != null && selectQueryData.getPrimaryKeys().size() > 1) {
			for (PrimaryKeyClass primaryKey : selectQueryData.getPrimaryKeys()) {
				if (primaryKeyString.isEmpty()) {
					primaryKeyString = primaryKey.getFieldName() + "= ?";
					primaryKeyCommaString = primaryKey.getFieldName();
				} else {
					primaryKeyString += " AND " + primaryKey.getFieldName() + "= ?";
					primaryKeyCommaString += "," + primaryKey.getFieldName();
				}
				if (!selectPrimaryKeyString.isEmpty()) {
					selectPrimaryKeyString += " AND ";
				}
				if (!deletePrimaryKeyString.isEmpty()) {
					deletePrimaryKeyString += " AND ";
				}
				primaryKeyControllerString = (primaryKeyControllerString.isEmpty()
						? "req.params." + primaryKey.getFieldName()
						: primaryKeyControllerString + ",req.params." + primaryKey.getFieldName());
				selectPrimaryKeyString += "t." + primaryKey.getFieldName() + "= ?";
				deletePrimaryKeyString += primaryKey.getFieldName() + "=?";
			}
			selectPrimaryKeyString = selectPrimaryKeyString.trim();
			deletePrimaryKeyString = deletePrimaryKeyString.trim();
			primaryKeyString = primaryKeyString.trim();
			primaryKeyControllerString = primaryKeyControllerString.trim();
		} else if (selectQueryData.getPrimaryKeys() != null && selectQueryData.getPrimaryKeys().size() == 1) {
			String fieldName = selectQueryData.getPrimaryKeys().get(0).getFieldName();
			selectPrimaryKeyString = "t." + fieldName + "= ?";
			deletePrimaryKeyString = fieldName + "=?";
			primaryKeyString = fieldName + "= ?";
			primaryKeyCommaString = fieldName;
			primaryKeyControllerString = "req.params." + fieldName;
		} else {
			List<ColumnModel> columns = dicColumn.getOrDefault(tableName, getTableColumns(tableName));
			dicColumn.put(tableName, columns);
			selectQueryData.setColumnList(columns);

			if (!columns.isEmpty()) {
				String field = columns.get(0).getField();
				selectPrimaryKeyString = "t." + field + "= ?";
				deletePrimaryKeyString = field + "=?";
				primaryKeyString = field + "= ?";
				primaryKeyCommaString = field;
				primaryKeyControllerString = "req.params." + field;
			}
		}

		selectAllQuery = selectAllQuery.replace("{tableName}", tableName)
				.replace("{joinQuery}", joinQueryString)
				.replace("{fkColumns}", fkColumnString);
		selectOneQuery = selectOneQuery.replace("{tableName}", tableName)
				.replace("{joinQuery}", joinQueryString)
				.replace("{fkColumns}", fkColumnString)
				.replace("{selectPrimaryKey}", selectPrimaryKeyString);
		searchQuery = searchQuery.replace("{tableName}", tableName)
				.replace("{joinQuery}", joinQueryString)
				.replace("{fkColumns}", fkColumnString)
				.replace("{searchCondition}",
						searchConditionString.trim().replaceFirst("^O", "").replaceFirst("^R", ""));
		selectAllRecordCountQuery = selectAllRecordCountQuery.replace("{tableName}", tableName)
				.replace("{joinQuery}", joinQueryString)
				.replace("{fkColumns}", fkColumnString);
		searchRecordCountQuery = searchRecordCountQuery.replace("{tableName}", tableName)
				.replace("{joinQuery}", joinQueryString)
				.replace("{fkColumns}", fkColumnString)
				.replace("{searchCondition}",
						searchConditionString.trim().replaceFirst("^O", "").replaceFirst("^R", ""));
		deleteQuery = deleteQuery.replace("{deletePrimaryKey}", deletePrimaryKeyString);
		String updateQuery = "";
		String insertQueryParam = "";

		for (InsertUpdateClass updateColumn : insertUpdateQueryData.getUpdateColumnList()) {
			insertQueryParam += " " + tableName + "." + updateColumn.getFieldName() + ",";
			updateQuery = updateQuery.isEmpty() ? updateColumn.getFieldName() + " = ?"
					: updateQuery + "," + updateColumn.getFieldName() + " = ?";
		}

		requiredFieldString = requiredFieldString.isEmpty() ? "true"
				: requiredFieldString.trim().replaceFirst("^\\|", "").replaceFirst("\\|$", "");
		finalQueryData.setPrimaryKeyControllerString(primaryKeyControllerString);
		finalQueryData.setPrimaryKeys(selectQueryData.getPrimaryKeys());
		finalQueryData.setSearchQuery(searchQuery);
		finalQueryData.setSelectAllQuery(selectAllQuery);
		finalQueryData.setSelectOneQuery(selectOneQuery);
		finalQueryData.setSelectAllRecordCountQuery(selectAllRecordCountQuery);
		finalQueryData.setSearchRecordCountQuery(searchRecordCountQuery);
		finalQueryData.setSelectQueryData(selectQueryData);
		finalQueryData.setPropertyListString(propertyListString);
		finalQueryData.setCreatePropertyListString(createPropertyListString);
		finalQueryData.setPrimaryKeyString(primaryKeyString);
		finalQueryData.setPrimaryKeyCommaString(primaryKeyCommaString);
		finalQueryData.setUpdateQuery(updateQuery);
		finalQueryData.setInsertQuery("INSERT INTO " + tableName + " set ?");
		insertQueryParam = insertQueryParam.replaceAll(",$", "") + "," + primaryKeyCommaString;
		finalQueryData.setUpdateParam(insertQueryParam);
		finalQueryData.setInsertParam("");
		finalQueryData.setDeleteQuery(deleteQuery);
		finalQueryData.setRequiredFieldString_CreateUpdate(requiredFieldString);

		return finalQueryData;
	}
}