package com.udojava.evalex;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

public class TestNested {

	@Test
	public void testNestedVars() {
		String x = "1";
		String y = "2";
		String z = "2*x + 3*y";
		String a = "2*x + 4*z";

		Expression e = new Expression(a);
		e.with("x", x);
		e.with("y", y);
		e.with("z", z);

		assertEquals("34", e.eval().toString());
	}

	@Test
	public void testReplacements() {
		Expression e = new Expression("3+a+aa+aaa").with("a", "1*x")
				.with("aa", "2*x").with("aaa", "3*x").with("x", "2");
		assertEquals("15", e.eval().toString());
	}

	@Test
	public void testNestedOutOfOrderVars(){
		String x = "7";
		String y = "5";
		String z = "a+b";
		String a = "p+q";
		String p = "b+q";
		String q = "x+y";
		String b = "x*2+y";
		Expression e = new Expression(z);
		e.with("q", q);
		e.with("p", p);
		e.with("a", a);
		e.with("b", b);
		e.with("x", x);
		e.with("y", y);
		// a+b = p+q+b = b+q+q+b = 2b+2q = 2(x*2 + y) + 2(x+y) = 2(7*2+5) + 2(7+5) = 
		String res = e.eval().toString();
		assertEquals("62", res);
	}

	@Test
	public void testNestedOutOfOrderVarsWithFunc(){
		String x = "7";
		String y = "5";
		String z = "a+b";
		String a = "p+q";
		String p = "b+q";
		String q = "myAvg(x,b)";
		String b = "x*2+y";
		Expression e = new Expression(z);
		e.with("q", q);
		e.with("p", p);
		e.with("a", a);
		e.with("b", b);
		e.with("x", x);
		e.with("y", y);
		e.addFunction(e.new Function("myAvg", 2){
		
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				assertEquals(2, parameters.size());
				BigDecimal two = new BigDecimal(2);
				BigDecimal res = (parameters.get(0).add(parameters.get(1)) ).divide(two);
				return res;
			}
		});
		// a+b = p+q+b = b+q+q+b = 2b+2q = 2(x*2 + y) + 2(myAvg(x+b)) = 2(7*2+5) + 2((7+(7*2+5))/2 = 38 + 26 = 64 
		String res = e.eval().toPlainString();
		assertEquals("64", res);
	}
}
