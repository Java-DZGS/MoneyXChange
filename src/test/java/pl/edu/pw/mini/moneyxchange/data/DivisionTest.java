package pl.edu.pw.mini.moneyxchange.data;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import pl.edu.pw.mini.moneyxchange.data.splitters.Division;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DivisionTest {

	@Test
	public void testSplitEqually() {
		Set<User> users = new HashSet<>();
		var A = new User("A", 1);
		var B = new User("B", 1);
		var C = new User("C", 1);
		var D = new User("D", 1);
		users.add(A);
		users.add(B);
		users.add(C);
		users.add(D);

		Money amount = Money.of(100, "PLN");

		var result = Division.splitEqually(users, amount);

		assertEquals(Money.of(25, "PLN"), result.get(A));
		assertEquals(Money.of(25, "PLN"), result.get(B));
		assertEquals(Money.of(25, "PLN"), result.get(C));
		assertEquals(Money.of(25, "PLN"), result.get(D));
	}

	@Test
	public void testSplitExactly() {
		Map<User, Double> users = new HashMap<>();
		var A = new User("A", 1);
		var B = new User("B", 1);
		var C = new User("C", 1);
		var D = new User("D", 1);
		users.put(A, 0.0);
		users.put(B, 10.0);
		users.put(C, 50.0);
		users.put(D, 1.0);

		var result = Division.splitExactly(users);

		assertNull(result.get(A));
		assertEquals(Money.of(10, "PLN"), result.get(B));
		assertEquals(Money.of(50, "PLN"), result.get(C));
		assertEquals(Money.of(1, "PLN"), result.get(D));
	}

	@Test
	public void testSplitByPercentages() {
		Map<User, Double> users = new HashMap<>();
		var A = new User("A", 1);
		var B = new User("B", 1);
		var C = new User("C", 1);
		var D = new User("D", 1);
		users.put(A, 0.0);
		users.put(B, 10.0);
		users.put(C, 50.0);
		users.put(D, 40.0);

		var result = Division.splitByPercentages(users, Money.of(100, "PLN"));

		assertNull(result.get(A));
		assertEquals(Money.of(10, "PLN"), result.get(B));
		assertEquals(Money.of(50, "PLN"), result.get(C));
		assertEquals(Money.of(40, "PLN"), result.get(D));
	}

	@Test
	public void testSplitByShares() {
		Map<User, Double> users = new HashMap<>();
		var A = new User("A", 1);
		var B = new User("B", 1);
		var C = new User("C", 1);
		var D = new User("D", 1);
		users.put(A, 0.0);
		users.put(B, 4.0);
		users.put(C, 5.0);
		users.put(D, 1.0);

		var result = Division.splitByShares(users, Money.of(60, "PLN"));

		assertNull(result.get(A));
		assertEquals(Money.of(24, "PLN"), result.get(B));
		assertEquals(Money.of(30, "PLN"), result.get(C));
		assertEquals(Money.of(6, "PLN"), result.get(D));
	}
}
