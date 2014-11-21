package udesc.bda.persistance;


public interface Database {

	public boolean save(DBEntity o);
	
	public void deleteAll();
	
	public boolean update(DBEntity o);
	
}
