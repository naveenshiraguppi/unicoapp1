package com.unico.gcdapp;

import junit.framework.TestCase;

public class GcdServiceTest extends TestCase{
	public void testGCD10_15()
    {
		int gcd = GcdService.getGcd(10, 15);
        assertEquals( 5, gcd );
    }
	public void testGCD15_10()
    {
		int gcd = GcdService.getGcd(10, 15);
        assertEquals( 5, gcd );
    }
	public void testGCD13_12()
    {
		int gcd = GcdService.getGcd(13, 12);
        assertEquals( 1, gcd );
    }
	public void testGCD12_12()
    {
		int gcd = GcdService.getGcd(12, 12);
        assertEquals( 12, gcd );
    }
}
