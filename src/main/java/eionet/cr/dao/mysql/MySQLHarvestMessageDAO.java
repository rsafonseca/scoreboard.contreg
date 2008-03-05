package eionet.cr.dao.mysql;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import eionet.cr.dao.DAOException;
import eionet.cr.dao.HarvestMessageDAO;
import eionet.cr.dto.HarvestMessageDTO;
import eionet.cr.dto.readers.HarvestMessageDTOReader;
import eionet.cr.util.sql.SQLUtil;

/**
 * 
 * @author heinljab
 *
 */
public class MySQLHarvestMessageDAO extends MySQLBaseDAO implements HarvestMessageDAO {

	/** */
	private static final String q_HarvestMessageByHarvestID = "select * from HARVEST_MESSAGE where HARVEST_ID=?";
	
	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestMessageDAO#findHarvestMessagesByHarvestID(java.lang.String)
	 */
	public List<HarvestMessageDTO> findHarvestMessagesByHarvestID(int harvestID) throws DAOException {
		
		List<Object> values = new ArrayList<Object>();
		values.add(new Integer(harvestID));
				
		Connection conn = null;
		HarvestMessageDTOReader rsReader = new HarvestMessageDTOReader();
		try{
			conn = getConnection();
			SQLUtil.executeQuery(q_HarvestMessageByHarvestID, values, rsReader, conn);
			List<HarvestMessageDTO>  list = rsReader.getResultList();
			return list;
		}
		catch (Exception e){
			throw new DAOException(e.getMessage(), e);
		}
		finally{
			closeConnection(conn);
		}
	}

	private static final String q_insertHarvestMessage =
		"insert into HARVEST_MESSAGE (HARVEST_ID, TYPE, MESSAGE, STACK_TRACE) values (?, ?, ?, ?)";
	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.HarvestMessageDAO#insertHarvestMessage(eionet.cr.dto.HarvestMessageDTO)
	 */
	public void insertHarvestMessage(HarvestMessageDTO harvestMessageDTO) throws DAOException {
		
		if (harvestMessageDTO==null)
			return;
		
		List<Object> values = new ArrayList<Object>();
		values.add(harvestMessageDTO.getHarvestId());
		values.add(harvestMessageDTO.getType());
		values.add(harvestMessageDTO.getMessage());
		values.add(harvestMessageDTO.getStackTrace());
		
		Connection conn = null;
		try{
			conn = getConnection();
			SQLUtil.executeUpdate(q_insertHarvestMessage, values, conn);
		}
		catch (Exception e){
			throw new DAOException(e.getMessage(), e);
		}
		finally{
			closeConnection(conn);
		}
	}


}
