package com.radaee.reader;

import java.util.Date;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyListView extends ListView implements OnScrollListener {
	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	//����ˢ��
	private final static int REFRESHING = 2;
	//ˢ�����
	private final static int DONE = 3;
	private final static int LOADING = 4;

	private final static int RATIO = 3;
	private LayoutInflater inflater;
	private LinearLayout headView;
	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	private boolean isRecored;
	private int headContentWidth;
	private int headContentHeight;
	private int startY;
	private int firstItemIndex;
	private int state;
	private boolean isBack;
	private OnRefreshListener refreshListener;
	private boolean isRefreshable;
	
	int i=1;

	public MyListView(Context context) {
		super(context);
		init(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setCacheColorHint(context.getResources().getColor(R.color.transparent));
		inflater = LayoutInflater.from(context);
		headView = (LinearLayout) inflater.inflate(R.layout.head, null);
		arrowImageView = (ImageView) headView.findViewById(R.id.head_arrowImageView);
		arrowImageView.setMinimumWidth(70);
		arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headView.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView.findViewById(R.id.head_lastUpdatedTextView);

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();
		Log.v("@@@@@@", "width:" + headContentWidth + " height:"+ headContentHeight);
		addHeaderView(headView, null, false);
		setOnScrollListener(this);

		animation = new RotateAnimation(0, -180,RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
			int arg3) {
		firstItemIndex = firstVisiableItem;
	}
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
					Log.v("@@@@@@", "ACTION_DOWN ���ǵ�  "+i+++"��" +1 );
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						Log.v("@@@@@@", "ACTION_UP PULL_To_REFRESH and changeHeaderViewByState()" +
								" ���ǵ�  "+i+++"��ǰ"+2 );
						changeHeaderViewByState();
						Log.v("@@@@@@", "ACTION_UP PULL_To_REFRESH and changeHeaderViewByState() " +
								"���ǵ�  "+i+++"����"+2 );
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						Log.v("@@@@@@", "ACTION_UP RELEASE_To_REFRESH changeHeaderViewByState() " +
								"���ǵ�  "+i+++"��" +3);
						changeHeaderViewByState();						
						onRefresh();
						Log.v("@@@@@@", "ACTION_UP RELEASE_To_REFRESH changeHeaderViewByState()" +
								" ���ǵ�  "+i+++"��" +3);
					}
				}
				isRecored = false;
				isBack = false;
				break;
			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				if (!isRecored && firstItemIndex == 0) {
					isRecored = true;
					startY = tempY;
					Log.v("@@@@@@", "ACTION_MOVE ���ǵ�  "+i+++"��" +4);
				}
				if (state != REFRESHING && isRecored && state != LOADING) {
					if (state == RELEASE_To_REFRESH) {
						setSelection(0);
						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
							Log.v("@@@@@@", "changeHeaderViewByState() ���ǵ�  "+i+++"��"+5 );
						}
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
							Log.v("@@@@@@", "ACTION_MOVE RELEASE_To_REFRESH 2  changeHeaderViewByState " +
									"���ǵ�  "+i+++"��" +6);
						}
					}
					if (state == PULL_To_REFRESH) {
						setSelection(0);
						if ((tempY - startY) / RATIO >= headContentHeight) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							Log.v("@@@@@@", "changeHeaderViewByState " +
									"���ǵ�  "+i+++"��ǰ"+7 );
							changeHeaderViewByState();
							Log.v("@@@@@@", "changeHeaderViewByState " +
									"���ǵ�  "+i+++"����"+7 );
						}
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
							Log.v("@@@@@@", "ACTION_MOVE changeHeaderViewByState PULL_To_REFRESH 2" +
									" ���ǵ�  "+i+++"��" +8);
						}
					}
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							Log.v("@@@@@@", "ACTION_MOVE DONE changeHeaderViewByState " +
									"���ǵ�  "+i+++"��ǰ" +9);
							changeHeaderViewByState();
							Log.v("@@@@@@", "ACTION_MOVE DONE changeHeaderViewByState " +
									"���ǵ�  "+i+++"����" +9);
						}
					}
					if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight+(tempY - startY) / RATIO, 0, 0);
						Log.v("@@@@@@", -1 * headContentHeight+(tempY - startY) / RATIO+
								"ACTION_MOVE PULL_To_REFRESH 3  ���ǵ�  "+i+++"��"+10 );
					}
					if (state == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO- headContentHeight, 0, 0);
						Log.v("@@@@@@", "ACTION_MOVE PULL_To_REFRESH 4 ���ǵ�  "+i+++"��" +11);
					}
				}
				break;
			}
		}
		return super.onTouchEvent(event);
	}

	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);
			tipsTextview.setText("���ͷ� ˢ��");
			Log.v("@@@@@@", "RELEASE_To_REFRESH ���ǵ�  "+i+++"��"+12 +"���ͷ� ˢ��" );
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);
				tipsTextview.setText("isBack  is true ������");
			} else {
				tipsTextview.setText("isBack  is false ������");
			}
			Log.v("@@@@@@", "PULL_To_REFRESH ���ǵ�  "+i+++"��" +13+
					"  changeHeaderViewByState()");
			break;
		case REFRESHING:
			headView.setPadding(0, 0, 0, 0);
			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText("���ڼ�����");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			Log.v("@@@@@@", "REFRESHING ���ǵ�  "+i+++"��" +"���ڼ����� ...REFRESHING");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.arrow);
			tipsTextview.setText("�Ѿ��������- DONE ");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			Log.v("@@@@@@", "DONE ���ǵ�  "+i+++"��" +"�Ѿ��������- DONE ");
			break;
		}
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public void onRefreshComplete() {
		state = DONE;
		lastUpdatedTextView.setText("�Ѽ������: " + new Date().toLocaleString());
		changeHeaderViewByState();
		Log.v("@@@@@@", "onRefreshComplete() �����á�����");
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();			
			Log.v("@@@@@@", "onRefresh�����ã����ǵ�  "+i+++"��" );
		}
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setAdapter(CategoryAdapter adapter) {
		lastUpdatedTextView.setText(new Date().toLocaleString());
		super.setAdapter(adapter);
	}
}