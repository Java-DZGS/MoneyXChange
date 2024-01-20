package pl.edu.pw.mini.moneyxchange.data;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import pl.edu.pw.mini.moneyxchange.utils.splitters.EqualSplitter;
import pl.edu.pw.mini.moneyxchange.utils.splitters.ExactSplitter;
import pl.edu.pw.mini.moneyxchange.utils.splitters.PercentageSplitter;
import pl.edu.pw.mini.moneyxchange.utils.splitters.SharesSplitter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DivisionTest {

	@Test
	public void testSplitEqually() {
		var A = new User("A");
		var B = new User("B");
		var C = new User("C");
		var D = new User("D");

		Money amount = Money.of(100, "PLN");

		EqualSplitter splitter = new EqualSplitter(amount);
		splitter.addUser(A, "");
		splitter.addUser(B, "");
		splitter.addUser(C, "");
		splitter.addUser(D, "");

		var result = splitter.split();

		assertEquals(Money.of(25, "PLN"), result.get(A));
		assertEquals(Money.of(25, "PLN"), result.get(B));
		assertEquals(Money.of(25, "PLN"), result.get(C));
		assertEquals(Money.of(25, "PLN"), result.get(D));
	}

	@Test
	public void testSplitExactly() {
		Money amount = Money.of(61, "PLN");

		var A = new User("A");
		var B = new User("B");
		var C = new User("C");
		var D = new User("D");

		ExactSplitter exactSplitter = new ExactSplitter(amount);
		exactSplitter.addUser(A, "0");
		exactSplitter.addUser(B, "10 PLN");
		exactSplitter.addUser(C, "50 PLN");
		exactSplitter.addUser(D, "1 PLN");

		var result = exactSplitter.split();

		assertNull(result.get(A));
		assertEquals(Money.of(10, "PLN"), result.get(B));
		assertEquals(Money.of(50, "PLN"), result.get(C));
		assertEquals(Money.of(1, "PLN"), result.get(D));
	}

	@Test
	public void testSplitByPercentages() {
		Money amount = Money.of(100, "PLN");
		PercentageSplitter percentageSplitter = new PercentageSplitter(amount);

		var A = new User("A");
		var B = new User("B");
		var C = new User("C");
		var D = new User("D");

		percentageSplitter.addUser(A, "0");
		percentageSplitter.addUser(B, "10");
		percentageSplitter.addUser(C, "50");
		percentageSplitter.addUser(D, "40");

		var result = percentageSplitter.split();

		assertNull(result.get(A));
		assertEquals(Money.of(10, "PLN"), result.get(B));
		assertEquals(Money.of(50, "PLN"), result.get(C));
		assertEquals(Money.of(40, "PLN"), result.get(D));
	}

	@Test
	public void testSplitByShares() {
		Money amount = Money.of(60, "PLN");
		SharesSplitter sharesSplitter = new SharesSplitter(amount);

		var A = new User("A");
		var B = new User("B");
		var C = new User("C");
		var D = new User("D");
		sharesSplitter.addUser(A, "0");
		sharesSplitter.addUser(B, "4");
		sharesSplitter.addUser(C, "5");
		sharesSplitter.addUser(D, "1");

		var result = sharesSplitter.split();

		assertNull(result.get(A));
		assertEquals(Money.of(24, "PLN"), result.get(B));
		assertEquals(Money.of(30, "PLN"), result.get(C));
		assertEquals(Money.of(6, "PLN"), result.get(D));
	}
}
