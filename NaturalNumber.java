/*
 * This class defines a natural number object, and puts the coefficients of the
 * number into a LinkedList. The class implements methods for simple arithmetic
 * operations using efficient algorithms and compares them to slower versions of
 * the same arithmetic operations implemented thruough different algorithms.
 */

import java.util.LinkedList;

public class NaturalNumber  {

	private int	base;

	private LinkedList<Integer>  coefficients;

	//  Constructors

	NaturalNumber(int base){

		//  If no string argument is given, then it constructs an empty list of coefficients.

		this.base = base;
		coefficients = new LinkedList<Integer>();
	}


	/*
	 *   The constructor builds a LinkedList of Integers where the integers need to be in [0,base).
	 *   The string only represents a base 10 number when the base is given to be 10
	 */


	NaturalNumber(String sBase10,  int base) throws Exception{
		int i;
		this.base = base;
		coefficients = new LinkedList<Integer>();
		if ((base <2) || (base > 10)){
			System.out.println("constructor error:  base must be between 2 and 10");
			throw new Exception();
		}

		int l = sBase10.length();
		for (int indx = 0; indx < l; indx++){
			i = sBase10.charAt(indx);
			if ((i >= 48) && (i - 48 < base))   // ascii value of symbol '0' is 48, symbol '1' is 49, etc.
                                                 // e.g.  to get the numerical value of '2',  we subtract
				                                 // the character value of '0' (48) from the character value of '2' (50)
				coefficients.addFirst( new Integer(i-48) );
			else{
				System.out.println("constructor error:  all coefficients should be non-negative and less than base");
				throw new Exception();
			}
		}
	}

	/*
	 *   Construct a natural number object for a number that has just one digit in [0, base).
	 *
	 *   This constructor acts as a helper.  It is not called from the Tester class.
	 */

	NaturalNumber(int i,  int base) throws Exception{
		this.base = base;
		coefficients = new LinkedList<Integer>();

		if ((i >= 0) && (i < base))
			coefficients.addFirst( new Integer(i) );
		else {
			System.out.println("constructor error: all coefficients should be non-negative and less than base");
			throw new Exception();
		}
	}

	/*
	 *   The plus method computes this.plus(b) where 'this' is a,  i.e. it computes a+b.
	 */

	public NaturalNumber plus( NaturalNumber  second) throws Exception{

		//  initialize the sum as an empty list of coefficients

		NaturalNumber sum = new NaturalNumber( this.base );

		if (this.base != second.base){
			System.out.println("ERROR: bases must be the same in an addition");
			throw new Exception();
		}

		NaturalNumber  firstClone  = this.clone();
		NaturalNumber  secondClone = second.clone();

		//   If the two numbers have a different polynomial order
		//   then pad the smaller one with zero coefficients.

		int   diff = firstClone.coefficients.size() - second.coefficients.size();
		while (diff < 0){  // second is bigger

			firstClone.coefficients.addLast(0);
			diff++;
		}
		while (diff > 0){  //  this is bigger
			secondClone.coefficients.addLast(0);
			diff--;
		}

		int carry = 0; //		this carry variable helps keep track across iterations
		for(int j = 0; j < firstClone.coefficients.size(); j++){
			//	this performs a single digit opetarion
			int tmp = firstClone.coefficients.get(j) + secondClone.coefficients.get(j);
			tmp = tmp + carry;
			/*
			 *		This if statement checks if the single digit addition is
			 * 		is bigger than the given base, in which case subtracts the
			 * 		base from the addition and sets carry to 1.
			 */
			if(tmp >= this.base){
				sum.coefficients.addLast(new Integer(tmp - this.base));
				carry = 1;
			}
			else{
				//		if tmp is smaller than the base, carry goes back to 0
				sum.coefficients.addLast(new Integer(tmp));
				carry = 0;
			}
		}
		/*
		 *		This checks if carry is 1 after all the single digit additions
		 *		are done, in which case it adds a 1 at the end of the coefficients.
		 *		This means that the overall sum gives a number of a higher magnitude.
		 */
		if(carry == 1){
			sum.coefficients.addLast(new Integer(1));
		}
		// this is just to remove leading zeros from the result
		while(sum.coefficients.size() > 1 && sum.coefficients.getLast() == 0){
			sum.coefficients.removeLast();
		}

		return sum;
	}



	public NaturalNumber slowTimes( NaturalNumber  multiplier) throws Exception{

		NaturalNumber prod  = new NaturalNumber(0, this.base);
		NaturalNumber one   = new NaturalNumber(1, this.base);
		for (NaturalNumber counter = new NaturalNumber(0, this.base);  counter.compareTo(multiplier) < 0;  counter = counter.plus(one) ){
			prod = prod.plus(this);
		}
		return prod;
	}

	/*   The multiply method computes this.multiply(b) where 'this' is a.
	 */

	public NaturalNumber times( NaturalNumber multiplicand) throws Exception{

		//  initialize product as an empty list of coefficients

		NaturalNumber product	= new NaturalNumber( this.base );

		if (this.base != multiplicand.base){
			System.out.println("ERROR: bases must be the same in a multiplication");
			throw new Exception();
		}

		int carry = 0; //		this helps keep track of single digit multiplications that are larger than the base
		for(int i = 0; i < this.coefficients.size(); i++){
			/*
			 *		line stores the result of the current iteration before
			 *		it is added to the overall multiplication at the end
			 *		of the iteration
			 */
			NaturalNumber line = new NaturalNumber(this.base);
			for(int j = 0; j < multiplicand.coefficients.size(); j++){
				//		here is where the single digit multiplication happens and carry is added
				int tmp = this.coefficients.get(i) * multiplicand.coefficients.get(j);
				tmp = tmp + carry;
				/*
				 *		This checks if tmp is bigger than the base, in which case it
				 *		takes modulo of the base and adds that number to the line coefficients
				 *		instead. Carry is also set to tmp divided by the base.
				 */
				if(tmp >= this.base){
					carry = tmp/this.base;
					line.coefficients.addLast(new Integer(tmp%this.base));
				}
				else{
					//		if tmp is smaller than the base, carry goes back to 0
					carry = 0;
					line.coefficients.addLast(new Integer(tmp));
				}
			}
			//		if carry is not 0 at the end of the inner loop then carry is added to the coefficients of line
			if(carry > 0){
				line.coefficients.addLast(new Integer(carry));
				carry = 0;
			}
			//		here line is multiplied times its base i times and then its added to the final result
			line.timesBaseToThePower(i);
			product = product.plus(line);
		}

		return product;
	}



	/*
	 *   The minus method computes this.minus(b) where 'this' is a, and a > b.
	 *   If a < b, then it throws an exception.
	 */

	public NaturalNumber  minus(NaturalNumber second) throws Exception{

		//  initialize the result (difference) as an empty list of coefficients

		NaturalNumber  difference = new NaturalNumber(this.base);

		if (this.base != second.base){
			System.out.println("ERROR: bases must be the same in a subtraction");
			throw new Exception();
		}

		NaturalNumber  first = this.clone();

		if (this.compareTo(second) < 0){
			System.out.println("Error: the subtraction a-b requires that a > b");
			throw new Exception();
		}

		NaturalNumber secondClone = second.clone();
		int sizeDiff = first.coefficients.size() - secondClone.coefficients.size();
		for(int i = 0; i < first.coefficients.size(); i++){
			//		this part of the loop only happens when i is smaller than the size of the second number
			if(i < first.coefficients.size() - sizeDiff){
				//		if the coefficient of the first number is bigger than the coefficient from the second
				//		number, then they are just subtracted and the difference gets added to the difference coefficients
				if(first.coefficients.get(i) >= secondClone.coefficients.get(i)){
					int tmp = first.coefficients.get(i) - secondClone.coefficients.get(i);
					difference.coefficients.addLast(new Integer(tmp));
				}
				//		If the coefficient of the second number is bigger, then the base is added to the coefficient
				//		of the first number before the single digit subtraction happens, and 1 is subtracted from
				//		the next coefficient of the first number.
				else{
					int tmp = (first.coefficients.get(i) + first.base) - secondClone.coefficients.get(i);
					difference.coefficients.addLast(new Integer(tmp));
					first.coefficients.set(i+1, first.coefficients.get(i+1) - 1);
				}
			}

			/*
			 *		This checks if the coefficients of the first number are not smaller than 0
			 *		when i is bigger than the size of the second number. If the number is
			 *		not smaller than 0 it just gets added it to the difference coefficients, otherwise
			 *		it adds the base to it, adds it to the difference coefficients
			 * 		and subtracts one to the next coefficient.
			 */
			else{
				if(first.coefficients.get(i) < 0){
					int tmp = first.coefficients.get(i) + this.base;
					first.coefficients.set(i, tmp);
					first.coefficients.set(i+1, first.coefficients.get(i+1) - 1);
				}
				difference.coefficients.addLast(new Integer(first.coefficients.get(i)));
			}
		}

		while ((difference.coefficients.size() > 1) &
				(difference.coefficients.getLast().intValue() == 0)){
			difference.coefficients.removeLast();
		}



		return difference;
	}


	public NaturalNumber slowDivide( NaturalNumber  divisor) throws Exception{

		NaturalNumber quotient = new NaturalNumber(0,base);
		NaturalNumber one = new NaturalNumber(1,base);
		NaturalNumber remainder = this.clone();
		while ( remainder.compareTo(divisor) >= 0 ){
			remainder = remainder.minus(divisor);
			quotient = quotient.plus(one);
		}
		return quotient;
	}


	/*
	 * The divide method divides 'this' by 'divisor' i.e. this.divide(divisor)
	 * When this method terminates, there is a remainder but it is ignored (not returned).
	 */

	public NaturalNumber divide( NaturalNumber  divisor ) throws Exception{

		//  initialize quotient as an empty list of coefficients

		NaturalNumber  quotient = new NaturalNumber(this.base);

		if (this.base != divisor.base){
			System.out.println("ERROR: bases must be the same in an division");
			throw new Exception();
		}

		NaturalNumber  remainder = this.clone();

		NaturalNumber divisorClone = divisor.clone();
		NaturalNumber dividend = this.clone();
		//		this checks if the divisor is bigger than the dividend, in which case it sets the quotient to 0
		if(divisor.compareTo(dividend) == 1){
			quotient.coefficients.addFirst(new Integer(0));
		}
		else{
			//		this first finds the size difference between divisor and dividend
			//		and multiplies the divisor by the base that number of times
			int diff = dividend.coefficients.size() - divisorClone.coefficients.size() + 1;
			divisorClone.timesBaseToThePower(diff - 1);
			//		if the divisor gets bigger than the dividend the it is divided by the base once
			if(divisorClone.compareTo(dividend) == 1){
				divisorClone.coefficients.removeFirst();
				diff--;
			}
			//		this for loop runs for as many times as 0s were added to the divisor, dividing
			//		the divisor by the base each iteration until we are back at the original divisor
			for(int i = 0; i < diff; i++){
				//		slowDivide is used to divide the dividend by the now bigger divisor, so it is a lot faster
				NaturalNumber nTmp = dividend.slowDivide(divisorClone);
				int tmp = nTmp.coefficients.getLast();
				//		the result of the division is added to the end of the quotient coefficients
				quotient.coefficients.addFirst(new Integer(tmp));
				//		here the result of the division times the divisor is subtracted from the dividend
				NaturalNumber subtractor = divisorClone.times(nTmp);
				//		this just removes leading 0s from the subtractor
				while((subtractor.coefficients.size() > 1) && (subtractor.coefficients.getLast() == 0)){
					subtractor.coefficients.removeLast();
				}
				dividend = dividend.minus(subtractor);
				//		remainder is set to the value of the dividend after the subtractor is subtracted
				remainder = dividend.clone();
				//		divisor is divided by base
				divisorClone.coefficients.removeFirst();
			}
		}
		return quotient;
	}

	//   Helper methods

	@Override
	public NaturalNumber  clone(){

		NaturalNumber copy = new NaturalNumber(this.base);
		for (int i=0; i < this.coefficients.size(); i++){
			copy.coefficients.addLast( new Integer( this.coefficients.get(i) ) );
		}
		return copy;
	}

	/*
	 *  The subtraction method (minus) computes a-b and requires that a>b.
	 *  The a.compareTo(b) method is useful for checking this condition.
	 *  It returns -1 if a < b,  it returns 0 if a == b,
	 *  and it returns 1 if a > b.
	 */

	private int 	compareTo(NaturalNumber second){

		int diff = this.coefficients.size() - second.coefficients.size();
		if (diff < 0)
			return -1;
		else if (diff > 0)
			return 1;
		else {

			boolean done = false;
			int i = this.coefficients.size() - 1;
			while (i >=0 && !done){
				diff = this.coefficients.get(i) - second.coefficients.get(i);
				if (diff < 0){
					return -1;
				}
				else if (diff > 0)
					return 1;
				else{
					i--;
				}
			}
			return 0;    //   if all coefficients are the same,  so numbers are equal.
		}
	}

	/*  computes  'this' * base^n
	 */

	private NaturalNumber timesBaseToThePower(int n){
		for (int i=0; i< n; i++){
			this.coefficients.addFirst(new Integer(0));
		}
		return this;
	}

	@Override
	public String toString(){
		String s = new String();
		for (Integer i : coefficients)
			s = i.toString() + s ;        //   coefficient i corresponds to degree i
		return "(" + s + ")_" + base;
	}

}
