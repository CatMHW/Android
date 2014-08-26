package com.radaee.pdf;


public class Path
{
	private static native int create();
	private static native void destroy( int hand );
	private static native void moveTo( int hand, float x, float y );
	private static native void lineTo( int hand, float x, float y );
	private static native void curveTo( int hand, float x1, float y1, float x2, float y2, float x3, float y3 );
	private static native void closePath( int hand );
	private static native int getNodeCount(int hand);
	private static native int getNode( int hand, int index, float[]pt );
	protected int m_hand = create();
	/**
	 * move to operation
	 * @param x
	 * @param y
	 */
	public void MoveTo( float x, float y )
	{
		moveTo( m_hand, x, y );
	}
	/**
	 * line to operation
	 * @param x
	 * @param y
	 */
	public void LineTo( float x, float y )
	{
		lineTo( m_hand, x, y );
	}
	public void CurveTo( float x1, float y1, float x2, float y2, float x3, float y3 )
	{
		curveTo( m_hand, x1, y1, x2, y2, x3, y3 );
	}
	/**
	 * close a contour.
	 */
	public void ClosePath()
	{
		closePath(m_hand);
	}
	/**
	 * free memory
	 */
	public void Destroy()
	{
		destroy(m_hand);
	}
	public int GetNodeCount()
	{
		return getNodeCount( m_hand );
	}
	/**
	 * get each node
	 * @param index range [0, GetNodeCount() - 1]
	 * @param pt output value: 2 elements coordinate point
	 * @return node type:<br/>
	 * 0: move to<br/>
	 * 1: line to<br/>
	 * 3: curve to, index, index + 1, index + 2 are all data<br/>
	 * 4: close operation<br/>
	 */
	public int GetNode( int index, float pt[] )
	{
		return getNode( m_hand, index, pt );
	}
}
