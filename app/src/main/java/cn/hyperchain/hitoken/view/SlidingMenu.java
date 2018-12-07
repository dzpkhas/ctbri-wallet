package cn.hyperchain.hitoken.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.hyperchain.hitoken.R;
import com.nineoldandroids.view.ViewHelper;

import cn.hyperchain.hitoken.utils.ScreenUtils;
import cn.hyperchain.hitoken.utils.StatusBarUtil;

public class SlidingMenu extends HorizontalScrollView
{
	/**
	 * 屏幕宽度
	 */
	private int mScreenWidth;
	/**
	 * dp
	 */
	private int mMenuRightPadding;
	/**
	 * 菜单的宽度
	 */
	private int mMenuWidth;
	private int mHalfMenuWidth;

	public boolean isOpen;

	private boolean once;

	private ViewGroup mMenu;
	private ViewGroup mContent;

	private ImageView iv;

	private TextView tabMain;

	private  Context mContext;

	public SlidingMenu(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);

	}

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mContext = context;
		mScreenWidth = ScreenUtils.getScreenWidth(context);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.SlidingMenu, defStyle, 0);

		int n = a.getIndexCount();

		for (int i = 0; i < n; i++)
		{
			int attr = a.getIndex(i);
			switch (attr)
			{
			case R.styleable.SlidingMenu_rightPadding:
				// 默认50
				mMenuRightPadding = a.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 50f,
								getResources().getDisplayMetrics()));// 默认为10DP
				break;
			}
		}
		a.recycle();
	}

	public SlidingMenu(Context context)
	{
		this(context, null, 0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		/**
		 * 显示的设置一个宽度
		 */
		if (!once)
		{
			LinearLayout wrapper = (LinearLayout) getChildAt(0);
			mMenu = (ViewGroup) wrapper.getChildAt(0);
			mContent = (ViewGroup) wrapper.getChildAt(1);

			iv = (ImageView) mContent.findViewById(R.id.iv_personal_center);
			tabMain = mContent.findViewById(R.id.tab_wallet);

			mMenuWidth = mScreenWidth - mMenuRightPadding;
			mHalfMenuWidth = mMenuWidth / 2;
			mMenu.getLayoutParams().width = mMenuWidth;
			mContent.getLayoutParams().width = mScreenWidth;

		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		if (changed)
		{
			// 将菜单隐藏
			this.scrollTo(mMenuWidth, 0);
			Log.e("slidingMenu","自动返回来。。。");
			once = true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		return false;
//		int action = ev.getAction();
//		switch (action)
//		{
//		// Up时，进行判断，如果显示区域大于菜单宽度一半则完全显示，否则隐藏
//			case MotionEvent.ACTION_UP:
//				int scrollX = getScrollX();
//				if (scrollX > mHalfMenuWidth)
//				{
//					this.smoothScrollTo(mMenuWidth, 0);
//					isOpen = false;
//					iv.setImageResource(R.mipmap.personal_center);
//					StatusBarUtil.setDarkMode((Activity)mContext);
//				} else
//				{
//					this.smoothScrollTo(0, 0);
//					isOpen = true;
//					iv.setImageResource(R.mipmap.housemessage_back);
//					StatusBarUtil.setLightMode((Activity)mContext);
//				}
//				return true;
//		}
//		return super.onTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}
	/**
	 * 打开菜单
	 */
	public void openMenu()
	{
		if (isOpen)
			return;
		this.smoothScrollTo(0, 0);
		isOpen = true;

	}

	/**
	 * 关闭菜单
	 */
	public void closeMenu()
	{
		if (isOpen)
		{
			this.smoothScrollTo(mMenuWidth, 0);
			isOpen = false;


		}
	}

	/**
	 * 切换菜单状态
	 */
	public void toggle()
	{
		if (isOpen)
		{
			closeMenu();

		} else
		{
			openMenu();

		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		super.onScrollChanged(l, t, oldl, oldt);
		float scale = l * 1.0f / mMenuWidth;
		float leftScale = 1 - 0.3f * scale;
		float rightScale = 0.8f + scale * 0.2f;
		
		ViewHelper.setScaleX(mMenu, leftScale);
		ViewHelper.setScaleY(mMenu, leftScale);
		ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
		ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.6f);

		ViewHelper.setPivotX(mContent, 0);
		ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
		ViewHelper.setScaleX(mContent, rightScale);
		ViewHelper.setScaleY(mContent, rightScale);

		if(oldl == mMenuWidth) {
			isOpen = true;
			iv.setImageResource(R.mipmap.housemessage_back);
			tabMain.setClickable(false);
			StatusBarUtil.setLightMode((Activity)mContext);
		} else if(oldl == 0) {
			isOpen = false;
			iv.setImageResource(R.mipmap.personal_center);
			tabMain.setClickable(true);
			StatusBarUtil.setDarkMode((Activity)mContext);
		}
		Log.e("slidingMenu","l = " + l +";t=" + t + ";oldl=" + oldl + ";oldt=" + oldt);
	}

}
