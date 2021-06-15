package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.yelp.model.Archi;
import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao {

	public void getAllBusiness(Map<String,Business> idMap){
		String sql = "SELECT * FROM Business";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(!idMap.containsKey(res.getString("business_id"))) {
				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				idMap.put(business.getBusinessId(), business);
				}
			}
			res.close();
			st.close();
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Review> getAllReviews(){
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Review review = new Review(res.getString("review_id"), 
						res.getString("business_id"),
						res.getString("user_id"),
						res.getDouble("stars"),
						res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<User> getAllUsers(){
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				User user = new User(res.getString("user_id"),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("name"),
						res.getDouble("average_stars"),
						res.getInt("review_count"));
				
				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getAllCitta(){
		String sql="SELECT DISTINCT b.city as citta FROM business b ORDER BY b.city ASC";
		List<String> result = new ArrayList<String>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {				
				result.add(res.getString("citta"));
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Business> getVertici(int anno,String citta, Map<String,Business> idMap, Map<String,Business> vertici){
		String sql="SELECT b.business_id as id FROM business b, reviews r "
				+ "WHERE b.business_id=r.business_id && b.city=? && YEAR(r.review_date)=? GROUP BY  b.business_id";
		List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, citta);
			st.setInt(2, anno);
			ResultSet res = st.executeQuery();
			while (res.next()) {				
				if(idMap.containsKey(res.getString("id"))) {
					result.add(idMap.get(res.getString("id")));
					vertici.put(res.getString("id"), idMap.get(res.getString("id")));
				}
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Archi> getArchi(int anno,String citta,Map<String,Business> vertici){
		String sql="SELECT b1.business_id AS id1, b2.business_id AS id2, (avg(r1.stars)-avg(r2.stars)) AS peso FROM business b1, business b2, reviews r1, reviews r2 "
				+ "WHERE b1.business_id=r1.business_id && b2.business_id=r2.business_id && b1.business_id>b2.business_id && b1.city=? && b2.city=b1.city "
				+ "&& YEAR(r1.review_date)=? && YEAR(r1.review_date)=YEAR(r2.review_date) "
				+ "GROUP BY  b1.business_id, b2.business_id HAVING peso !=0";
		List<Archi> result = new ArrayList<Archi>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, citta);
			st.setInt(2, anno);
			ResultSet res = st.executeQuery();
			while (res.next()) {				
				if(vertici.containsKey(res.getString("id1")) && vertici.containsKey(res.getString("id2"))) {
					Archi a=new Archi(vertici.get(res.getString("id1")), vertici.get(res.getString("id2")), res.getDouble("peso"));
					result.add(a);
				}
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
