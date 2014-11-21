package udesc.bda.persistance;

import udesc.bda.stock.persistance.StockDatabase;

public class DBFactory {
	
	
	public static StockDatabase getStockDB() {
		if (DatabaseType.MONGODB == DBConfiguration.dbType) {
			return new udesc.bda.stock.persistance.mongo.StockDB();
		}
		
		if (DatabaseType.MYSQL == DBConfiguration.dbType) {
			return new udesc.bda.stock.persistance.mysql.StockDB();
		}
		return null;
	}

	public static Database getStockRequestDB() {
		if (DatabaseType.MONGODB == DBConfiguration.dbType) {
			return new udesc.bda.stock.persistance.mongo.StockRequestDB();
		}
		
		if (DatabaseType.MYSQL == DBConfiguration.dbType) {
			return new udesc.bda.stock.persistance.mysql.StockRequestDB();
		}
		return null;
	}
	
	public static Database getOrderDB() {
		if (DatabaseType.MONGODB == DBConfiguration.dbType) {
			return new udesc.bda.order.persistance.mongo.OrderDB();
		}
		
		if (DatabaseType.MYSQL == DBConfiguration.dbType) {
			return new udesc.bda.order.persistance.mysql.OrderDB();
		}
		return null;
	}
	
	public static Database getOrderRequestDB() {
		if (DatabaseType.MONGODB == DBConfiguration.dbType) {
			return new udesc.bda.order.persistance.mongo.OrderRequestDB();
		}
		
		if (DatabaseType.MYSQL == DBConfiguration.dbType) {
			return new udesc.bda.order.persistance.mysql.OrderRequestDB();
		}
		return null;
	}
	

}
