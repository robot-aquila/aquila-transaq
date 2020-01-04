package ru.prolib.aquila.transaq.remote;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class IDTest {
	private Set<Integer> markets1, markets2;
	private ID.MP id_mp;
	private ID.SP id_sp;
	private ID.FP id_fp;
	private ID.FM id_fm;
	private ID.FC id_fc;
	private ID.SL id_sl;
	private ID.UL id_ul;

	@Before
	public void setUp() throws Exception {
		markets1 = new HashSet<>();
		markets1.add(1);
		markets1.add(4);
		markets2 = new HashSet<>();
		markets2.add(15);
		markets2.add(14);
		id_mp = new ID.MP("xxx1234", "CASH", "T0");
		id_sp = new ID.SP("cookie", "GAZP", 1, "T1");
		id_fp = new ID.FP("cookie", "GAZP", markets1);
		id_fm = new ID.FM("cookie");
		id_fc = new ID.FC("wookie", markets1);
		id_sl = new ID.SL("bookie", markets2);
		id_ul = new ID.UL("boogie");
	}

	@Test
	public void testMP_Getters() {
		assertEquals(ID.Type.MONEY_POSITION, id_mp.getType());
		assertEquals("xxx1234", id_mp.getClientID());
		assertEquals("CASH", id_mp.getAsset());
		assertEquals("T0", id_mp.getRegister());
	}
	
	@Test
	public void testMP_ToString() {
		String expected = "ID.MP[clientID=xxx1234,asset=CASH,register=T0]";
		assertEquals(expected, id_mp.toString());
	}
	
	@Test
	public void testMP_HashCode() {
		int expected = new HashCodeBuilder(6009817, 5501)
				.append("xxx1234")
				.append("CASH")
				.append("T0")
				.build();
		assertEquals(expected, id_mp.hashCode());
	}
	
	@Test
	public void testMP_Equals_SpecialCases() {
		assertTrue(id_mp.equals(id_mp));
		assertFalse(id_mp.equals(null));
		assertFalse(id_mp.equals(this));
	}
	
	@Test
	public void testMP_Equals() {
		Variant<String>
			vCID = new Variant<>("xxx1234", "cooker"),
			vAst = new Variant<>(vCID, "CASH", "BASH"),
			vReg = new Variant<>(vAst, "T0", "T1");
		Variant<?> iterator = vReg;
		int found_cnt = 0;
		ID.MP x, found = null;
		do {
			x = new ID.MP(vCID.get(), vAst.get(), vReg.get());
			if ( id_mp.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals("xxx1234", found.getClientID());
		assertEquals("CASH", found.getAsset());
		assertEquals("T0", found.getRegister());
	}
	
	@Test
	public void testSP_Getters() {
		assertEquals(ID.Type.SEC_POSITION, id_sp.getType());
		assertEquals("cookie", id_sp.getClientID());
		assertEquals("GAZP", id_sp.getSecCode());
		assertEquals(1, id_sp.getMarketID());
		assertEquals("T1", id_sp.getRegister());
	}
	
	@Test
	public void testSP_ToString() {
		String expected = "ID.SP[clientID=cookie,secCode=GAZP,marketID=1,register=T1]";
		
		assertEquals(expected, id_sp.toString());
	}
	
	@Test
	public void testSP_HashCode() {
		int expected = new HashCodeBuilder(297159, 901)
				.append("cookie")
				.append("GAZP")
				.append(1)
				.append("T1")
				.build();
		
		assertEquals(expected, id_sp.hashCode());
	}
	
	@Test
	public void testSP_Equals_SpecialCases() {
		assertTrue(id_sp.equals(id_sp));
		assertFalse(id_sp.equals(null));
		assertFalse(id_sp.equals(this));
	}
	
	@Test
	public void testSP_Equals() {
		Variant<String> vCID = new Variant<>("cookie", "wookie");
		Variant<String> vSec = new Variant<>(vCID, "GAZP", "SBER");
		Variant<Integer> vMkt = new Variant<>(vSec, 1, 14);
		Variant<String> vReg = new Variant<>(vMkt, "T1", "T0");
		Variant<?> iterator = vReg;
		int found_cnt = 0;
		ID.SP x, found = null;
		do {
			x = new ID.SP(vCID.get(), vSec.get(), vMkt.get(), vReg.get());
			if ( id_sp.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals("cookie", found.getClientID());
		assertEquals("GAZP", found.getSecCode());
		assertEquals(1, found.getMarketID());
		assertEquals("T1", found.getRegister());
	}

	@Test
	public void testFP_Getters() {
		assertEquals(ID.Type.FORTS_POSITION, id_fp.getType());
		assertEquals("cookie", id_fp.getClientID());
		assertEquals("GAZP", id_fp.getSecCode());
		assertEquals(markets1, id_fp.getMarkets());
	}
	
	@Test
	public void testFP_ToString() {
		String expected = "ID.FP[clientID=cookie,secCode=GAZP,markets=[1, 4]]";
		
		assertEquals(expected, id_fp.toString());
	}
	
	@Test
	public void testFP_HashCode() {
		int expected = new HashCodeBuilder(900163, 309)
				.append("cookie")
				.append("GAZP")
				.append(markets1)
				.build();
		
		assertEquals(expected, id_fp.hashCode());
	}
	
	@Test
	public void testFP_Equals_SpecialCases() {
		assertTrue(id_fp.equals(id_fp));
		assertFalse(id_fp.equals(null));
		assertFalse(id_fp.equals(this));
	}
	
	@Test
	public void testFP_Equals() {
		Variant<String> vCID = new Variant<>("cookie", "wookie");
		Variant<String> vSec = new Variant<>(vCID, "GAZP", "SBER");
		Variant<Set<Integer>> vMkt = new Variant<>(vSec, markets1, markets2);
		Variant<?> iterator = vMkt;
		int found_cnt = 0;
		ID.FP x, found = null;
		do {
			x = new ID.FP(vCID.get(), vSec.get(), vMkt.get());
			if ( id_fp.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals("cookie", found.getClientID());
		assertEquals("GAZP", found.getSecCode());
		assertEquals(markets1, found.getMarkets());
	}
	
	@Test
	public void testFM_Getters() {
		assertEquals(ID.Type.FORTS_MONEY, id_fm.getType());
		assertEquals("cookie", id_fm.getClientID());
	}
	
	@Test
	public void testFM_ToString() {
		String expected = "ID.FM[clientID=cookie]";
		
		assertEquals(expected, id_fm.toString());
	}
	
	@Test
	public void testFM_HashCode() {
		int expected = new HashCodeBuilder(7009715, 51)
				.append("cookie")
				.build();

		assertEquals(expected, id_fm.hashCode());
	}
	
	@Test
	public void testFM_Equals_SpecialCases() {
		assertTrue(id_fm.equals(id_fm));
		assertFalse(id_fm.equals(null));
		assertFalse(id_fm.equals(this));
	}
	
	@Test
	public void testFM_Equals() {
		Variant<String> vCID = new Variant<>("cookie", "wookie");
		Variant<?> iterator = vCID;
		int found_cnt = 0;
		ID.FM x, found = null;
		do {
			x = new ID.FM(vCID.get());
			if ( id_fm.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals("cookie", found.getClientID());
	}

	@Test
	public void testFC_Getters() {
		assertEquals(ID.Type.FORTS_COLLATERALS, id_fc.getType());
		assertEquals("wookie", id_fc.getClientID());
		assertEquals(markets1, id_fc.getMarkets());
	}
	
	@Test
	public void testFC_ToString() {
		String expected = "ID.FC[clientID=wookie,markets=[1, 4]]";
		
		assertEquals(expected, id_fc.toString());
	}
	
	@Test
	public void testFC_HashCode() {
		int expected = new HashCodeBuilder(855501, 77)
				.append("wookie")
				.append(markets1)
				.build();
		
		assertEquals(expected, id_fc.hashCode());
	}
	
	@Test
	public void testFC_Equals_SpecialCases() {
		assertTrue(id_fc.equals(id_fc));
		assertFalse(id_fc.equals(null));
		assertFalse(id_fc.equals(this));
	}
	
	@Test
	public void testFC_Equals() {
		Variant<String> vCID = new Variant<>("wookie", "bakka");
		Variant<Set<Integer>> vMkt = new Variant<>(vCID, markets1, markets2);
		Variant<?> iterator = vMkt;
		int found_cnt = 0;
		ID.FC x, found = null;
		do {
			x = new ID.FC(vCID.get(), vMkt.get());
			if ( id_fc.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals("wookie", found.getClientID());
		assertEquals(markets1, found.getMarkets());
	}

	@Test
	public void testSL_Getters() {
		assertEquals(ID.Type.SPOT_LIMIT, id_sl.getType());
		assertEquals("bookie", id_sl.getClientID());
		assertEquals(markets2, id_sl.getMarkets());
	}
	
	@Test
	public void testSL_ToString() {
		String expected = "ID.SL[clientID=bookie,markets=[14, 15]]";
		
		assertEquals(expected, id_sl.toString());
	}
	
	@Test
	public void testSL_HashCode() {
		int expected = new HashCodeBuilder(9997861, 331)
				.append("bookie")
				.append(markets2)
				.build();
		
		assertEquals(expected, id_sl.hashCode());
	}
	
	@Test
	public void testSL_Equals_SpecialCases() {
		assertTrue(id_sl.equals(id_sl));
		assertFalse(id_sl.equals(null));
		assertFalse(id_sl.equals(this));
	}
	
	@Test
	public void testSL_Equals() {
		Variant<String> vCID = new Variant<>("bookie", "gookie");
		Variant<Set<Integer>> vMkt = new Variant<>(vCID, markets2, markets1);
		Variant<?> iterator = vMkt;
		int found_cnt = 0;
		ID.SL x, found = null;
		do {
			x = new ID.SL(vCID.get(), vMkt.get());
			if ( id_sl.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals("bookie", found.getClientID());
		assertEquals(markets2, found.getMarkets());
	}
	
	@Test
	public void testUL_Getters() {
		assertEquals(ID.Type.UNITED_LIMITS, id_ul.getType());
		assertEquals("boogie", id_ul.getUnionCode());
	}

	@Test
	public void testUL_ToString() {
		String expected = "ID.UL[unionCode=boogie]";
		
		assertEquals(expected, id_ul.toString());
	}
	
	@Test
	public void testUL_HashCode() {
		int expected = new HashCodeBuilder(7781, 13)
				.append("boogie")
				.build();
		
		assertEquals(expected, id_ul.hashCode());
	}

	@Test
	public void testUL_Equals_SpecialCases() {
		assertTrue(id_ul.equals(id_ul));
		assertFalse(id_ul.equals(null));
		assertFalse(id_ul.equals(this));
	}

	@Test
	public void testUL_Equals() {
		assertTrue(id_ul.equals(new ID.UL("boogie")));
		assertFalse(id_ul.equals(new ID.UL("babaka")));
	}

}
