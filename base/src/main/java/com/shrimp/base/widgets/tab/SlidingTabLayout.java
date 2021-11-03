package com.shrimp.base.widgets.tab;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shrimp.base.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * 布局结构：
 * <HorizontalScrollView>
 * <LinearLayout>
 * <RelativeLayout>
 * <TextView/>                            -->  title
 * <com.flyco.tablayout.widget.MsgView/>  -->  newMsg
 * </RelativeLayout>
 * <RelativeLayout>
 * <TextView/>                            -->  title
 * <com.flyco.tablayout.widget.MsgView/>  -->  newMsg
 * </RelativeLayout>
 * ...
 * </LinearLayout>
 * </HorizontalScrollView>
 */
public class SlidingTabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener {
    // region attrs
    private int mDotSize;//setDot()时消息圆点的宽高
    private int mTipBackgroundColor = -1;//新消息的背景颜色
    //TipIconView 显示在右上角的一个icon
    private Drawable mTipBackgroundDrawable;//新消息的背景图片
    private int mTipIconSize;
    private int mTipIconTopMargin;
    private int mTipMinWidth = -1;
    private int mTipTextSize;//单位sp
    private int mTipTextColor;
    private int mTipViewHeight;
    private int mTipViewPadding = -1;
    private int mTipMarginTop; //设置为-1则为不进行偏移，默认如果有设置控件高度则会进行居中偏移
    private int mTipMarginLeft;

    private final Context mContext;
    private ViewPager mViewPager;
    private ArrayList<String> mTitles;
    private final LinearLayout mTabsContainer;
    private int mCurrentTab;
    private float mCurrentPositionOffset;
    private int mTabCount;
    /**
     * 用于绘制显示器
     */
    private final Rect mIndicatorRect = new Rect();
    /**
     * 用于实现滚动居中
     */
    private final Rect mTabRect = new Rect();
    private GradientDrawable mIndicatorDrawable = new GradientDrawable();
    private Drawable mIndicatorImgDrawable;

    private final Paint mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mTrianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mTrianglePath = new Path();
    private static final int STYLE_NORMAL = 0; //常规的横线
    private static final int STYLE_TRIANGLE = 1; //三角形
    private static final int STYLE_BLOCK = 2; //背景色块
    private int mIndicatorStyle = STYLE_NORMAL; //indicator索引的样式
    private byte mTextGravity = TEXT_GRAVITY_CENTER;

    public static final byte TEXT_GRAVITY_CENTER = 1;
    //需要同时设置控件高度，否则wrap_content会导致高度match
    public static final byte TEXT_GRAVITY_CENTER_HORIZONTAL_WITH_BOTTOM = 2;

    /**
     * tab
     */
    private float mTabPadding;
    private boolean needRemoveFirstItemPaddingLeft;
    private boolean mTabSpaceEqual;//设置tab等分宽度
    private float mTabWidth;
    private int mTabMarginLeft;
    private Drawable mTabSelectDrawable;
    private Drawable mTabUnSelectDrawable;
    /**
     * drawableLeft的样式，注意：和文本互斥，即有DrawableLeft的时候不会显示文本
     * mDrawableLeftArray/mDrawableLeftArraySelected需要同时设置
     */
    private final SparseIntArray mDrawableLeftArray;
    private final SparseIntArray mDrawableLeftArraySelected;

    /**
     * indicator
     */
    private int mIndicatorColor; //默认使用
    private int[] mIndicatorColorList; //渐变色组，仅当mIndicatorStyle==STYLE_NORMAL时生效
    private float mIndicatorHeight;
    private float mIndicatorWidth;
    private float mIndicatorCornerRadius;
    private float mIndicatorMarginLeft;
    private float mIndicatorMarginTop;
    private float mIndicatorMarginRight;
    private float mIndicatorMarginBottom;
    private int mIndicatorGravity;
    private boolean mIndicatorWidthEqualTitle; //是否和标题等宽

    /**
     * underline
     */
    private int mUnderlineColor;
    private float mUnderlineHeight;
    private int mUnderlineGravity;

    /**
     * divider
     */
    private int mDividerColor;
    private float mDividerWidth;
    private float mDividerPadding;

    /**
     * title
     */
    public static final int TEXT_BOLD_NONE = 0;
    public static final int TEXT_BOLD_WHEN_SELECT = 1;
    public static final int TEXT_BOLD_BOTH = 2;
    private int mTextMarginBottom;//标题靠底部对齐之后进行一个margin偏移
    private float mTextsize, mUnSelectTextSize;
    private int mTextSelectColor;
    private int mTextUnselectColor;
    private int mTextBold;
    private boolean mTextAllCaps;

    private int mLastScrollX;
    private int mHeight;
    private boolean mSnapOnTabClick;
    // endregion

    public SlidingTabLayout(Context context) {
        this(context, null, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFillViewport(true);//设置滚动视图是否可以伸缩其内容以填充视口
        setWillNotDraw(false);//重写onDraw方法,需要调用这个方法来清除flag
        setClipChildren(false);
        setClipToPadding(false);

        this.mContext = context;
        mDrawableLeftArray = new SparseIntArray();
        mDrawableLeftArraySelected = new SparseIntArray();
        mTabsContainer = new LinearLayout(context);
        addView(mTabsContainer);

        obtainAttributes(context, attrs);

        //get layout_height
        String height = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");

        if (!height.equals(ViewGroup.LayoutParams.MATCH_PARENT + "") && !height.equals(ViewGroup.LayoutParams.WRAP_CONTENT + "")) {
            int[] systemAttrs = {android.R.attr.layout_height};
            TypedArray a = context.obtainStyledAttributes(attrs, systemAttrs);
            mHeight = a.getDimensionPixelSize(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            a.recycle();
        }
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        @SuppressLint("CustomViewStyleable") TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingTabLayout);

        mIndicatorStyle = ta.getInt(R.styleable.SlidingTabLayout_tl_indicator_style, STYLE_NORMAL);
        mIndicatorColor = ta.getColor(R.styleable.SlidingTabLayout_tl_indicator_color, Color.parseColor(mIndicatorStyle == STYLE_BLOCK ? "#4B6A87" : "#ffffff"));
        mIndicatorHeight = ta.getDimension(R.styleable.SlidingTabLayout_tl_indicator_height,
                dp2px(mIndicatorStyle == STYLE_TRIANGLE ? 4 : (mIndicatorStyle == STYLE_BLOCK ? -1 : 2)));
        mIndicatorWidth = ta.getDimension(R.styleable.SlidingTabLayout_tl_indicator_width, dp2px(mIndicatorStyle == STYLE_TRIANGLE ? 10 : -1));
        mIndicatorCornerRadius = ta.getDimension(R.styleable.SlidingTabLayout_tl_indicator_corner_radius, dp2px(mIndicatorStyle == STYLE_BLOCK ? -1 : 0));
        mIndicatorMarginLeft = ta.getDimension(R.styleable.SlidingTabLayout_tl_indicator_margin_left, dp2px(0));
        mIndicatorMarginTop = ta.getDimension(R.styleable.SlidingTabLayout_tl_indicator_margin_top, dp2px(mIndicatorStyle == STYLE_BLOCK ? 7 : 0));
        mIndicatorMarginRight = ta.getDimension(R.styleable.SlidingTabLayout_tl_indicator_margin_right, dp2px(0));
        mIndicatorMarginBottom = ta.getDimension(R.styleable.SlidingTabLayout_tl_indicator_margin_bottom, dp2px(mIndicatorStyle == STYLE_BLOCK ? 7 : 0));
        mIndicatorGravity = ta.getInt(R.styleable.SlidingTabLayout_tl_indicator_gravity, Gravity.BOTTOM);
        mIndicatorWidthEqualTitle = ta.getBoolean(R.styleable.SlidingTabLayout_tl_indicator_width_equal_title, false);

        mUnderlineColor = ta.getColor(R.styleable.SlidingTabLayout_tl_underline_color, Color.parseColor("#ffffff"));
        mUnderlineHeight = ta.getDimension(R.styleable.SlidingTabLayout_tl_underline_height, dp2px(0));
        mUnderlineGravity = ta.getInt(R.styleable.SlidingTabLayout_tl_underline_gravity, Gravity.BOTTOM);

        mDividerColor = ta.getColor(R.styleable.SlidingTabLayout_tl_divider_color, Color.parseColor("#ffffff"));
        mDividerWidth = ta.getDimension(R.styleable.SlidingTabLayout_tl_divider_width, dp2px(0));
        mDividerPadding = ta.getDimension(R.styleable.SlidingTabLayout_tl_divider_padding, dp2px(12));

        mTextsize = ta.getDimension(R.styleable.SlidingTabLayout_tl_textsize, sp2px(15));
        mTextSelectColor = ta.getColor(R.styleable.SlidingTabLayout_tl_textSelectColor, Color.parseColor("#ffffff"));
        mTextUnselectColor = ta.getColor(R.styleable.SlidingTabLayout_tl_textUnselectColor, Color.parseColor("#AAffffff"));
        mTextBold = ta.getInt(R.styleable.SlidingTabLayout_tl_textBold, TEXT_BOLD_NONE);
        mTextAllCaps = ta.getBoolean(R.styleable.SlidingTabLayout_tl_textAllCaps, false);

        mTabSpaceEqual = ta.getBoolean(R.styleable.SlidingTabLayout_tl_tab_space_equal, false);
        mTabWidth = ta.getDimension(R.styleable.SlidingTabLayout_tl_tab_width, dp2px(-1));
        mTabPadding = ta.getDimension(R.styleable.SlidingTabLayout_tl_tab_padding, mTabSpaceEqual || mTabWidth > 0 ? dp2px(0) : dp2px(20));

        ta.recycle();
    }

    /**
     * 关联ViewPager
     */
    public void setViewPager(ViewPager vp) {
        if (vp == null || vp.getAdapter() == null) {
            throw new IllegalStateException("ViewPager or ViewPager adapter can not be NULL !");
        }

        mViewPager = vp;
        mViewPager.removeOnPageChangeListener(this);
        mViewPager.addOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    /**
     * 关联ViewPager,用于不想在ViewPager适配器中设置titles数据的情况
     */
    public void setViewPager(ViewPager vp, String[] titles) {
        if (vp == null || vp.getAdapter() == null) {
            throw new IllegalStateException("ViewPager or ViewPager adapter can not be NULL !");
        }

        if (titles == null || titles.length == 0) {
            throw new IllegalStateException("Titles can not be EMPTY !");
        }

        if (titles.length != vp.getAdapter().getCount()) {
            throw new IllegalStateException("Titles length must be the same as the page count !");
        }

        mViewPager = vp;
        mTitles = new ArrayList<>();
        Collections.addAll(mTitles, titles);

        mViewPager.removeOnPageChangeListener(this);
        mViewPager.addOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    /**
     * 关联ViewPager,用于连适配器都不想自己实例化的情况
     */
    public void setViewPager(ViewPager vp, String[] titles, FragmentActivity fa, ArrayList<Fragment> fragments) {
        if (vp == null) {
            throw new IllegalStateException("ViewPager can not be NULL !");
        }

        if (titles == null || titles.length == 0) {
            throw new IllegalStateException("Titles can not be EMPTY !");
        }

        this.mViewPager = vp;
        this.mViewPager.setAdapter(new InnerPagerAdapter(fa.getSupportFragmentManager(), fragments, titles));

        this.mViewPager.removeOnPageChangeListener(this);
        this.mViewPager.addOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    //解绑ViewPager使用
    public void setData(String[] titles) {
        if (mViewPager != null) {
            throw new IllegalStateException("Can't bind ViewPager !");
        }
        if (titles == null || titles.length == 0) {
            throw new IllegalStateException("Titles can not be EMPTY !");
        }
        mTitles = new ArrayList<>();
        Collections.addAll(mTitles, titles);

        notifyDataSetChanged();
    }

    /**
     * 更新数据
     */
    public void notifyDataSetChanged() {
        mTabsContainer.removeAllViews();
        if ((mViewPager == null || mViewPager.getAdapter() == null) && mTitles == null) return;
        try {
            this.mTabCount = mTitles == null ? mViewPager.getAdapter().getCount() : mTitles.size();
            View tabView;
            for (int i = 0; i < mTabCount; i++) {
                tabView = View.inflate(mContext, R.layout.item_custom_sliding_tab_layout, null);
                CharSequence pageTitle = mTitles == null ? mViewPager.getAdapter().getPageTitle(i) : mTitles.get(i);
                addTab(i, Objects.requireNonNull(pageTitle).toString(), tabView);
            }
            updateTabStyles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建并添加tab
     */
    private void addTab(final int position, String title, View tabView) {
        TextView tv_tab_title = tabView.findViewById(R.id.tv_tab_title);
        if (tv_tab_title != null) {
            if (mDrawableLeftArray.get(position) > 0) {
                tv_tab_title.setCompoundDrawablesWithIntrinsicBounds(mDrawableLeftArray.get(position), 0, 0, 0);
            } else if (title != null) {
                tv_tab_title.setText(title);
            }
        }

        tabView.setOnClickListener(v -> {
            int position1 = mTabsContainer.indexOfChild(v);
            if (position1 != -1) {
                if (mViewPager != null) {
                    if (mViewPager.getCurrentItem() != position1) {
                        if (mSnapOnTabClick) {
                            mViewPager.setCurrentItem(position1, false);
                        } else {
                            mViewPager.setCurrentItem(position1);
                        }

                        if (mListener != null) {
                            mListener.onTabSelect(position1);
                        }
                    } else {
                        if (mListener != null) {
                            mListener.onTabReselect(position1);
                        }
                    }
                } else {
                    if (mCurrentTab == position1) {
                        if (mListener != null) {
                            mListener.onTabReselect(position1);
                        }
                    } else {
                        mCurrentTab = position1;
                        scrollToCurrentTab();
                        updateTabSelection(position1);
                        if (mListener != null) {
                            mListener.onTabSelect(position1);
                        }
                    }
                }
            }
        });

        /* 每一个Tab的布局参数 */
        LinearLayout.LayoutParams lp_tab = mTabSpaceEqual ?
                new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f) :
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        if (mTabWidth > 0) {
            lp_tab = new LinearLayout.LayoutParams((int) mTabWidth, LayoutParams.MATCH_PARENT);
        }

        lp_tab.leftMargin = position > 0 ? mTabMarginLeft : 0;

        mTabsContainer.addView(tabView, position, lp_tab);
    }

    private void updateTabStyles() {
        for (int i = 0; i < mTabCount; i++) {
            View v = mTabsContainer.getChildAt(i);
            TextView tv_tab_title = v.findViewById(R.id.tv_tab_title);
            if (tv_tab_title != null) {
                if (mDrawableLeftArray.get(i) <= 0) {
                    v.setBackground(i == mCurrentTab ? mTabSelectDrawable : mTabUnSelectDrawable);
                    tv_tab_title.setTextColor(i == mCurrentTab ? mTextSelectColor : mTextUnselectColor);
                    tv_tab_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, i == mCurrentTab || mUnSelectTextSize <= 0 ? mTextsize : mUnSelectTextSize);
                    if (mTextAllCaps) {
                        tv_tab_title.setText(tv_tab_title.getText().toString().toUpperCase());
                    }
                    if (mTextBold == TEXT_BOLD_BOTH) {
                        tv_tab_title.getPaint().setFakeBoldText(true);
                    } else if (mTextBold == TEXT_BOLD_NONE) {
                        tv_tab_title.getPaint().setFakeBoldText(false);
                    } else if (mTextBold == TEXT_BOLD_WHEN_SELECT && i == mCurrentTab) {
                        tv_tab_title.getPaint().setFakeBoldText(true);
                    }
                }
                tv_tab_title.setPadding(i == 0 && needRemoveFirstItemPaddingLeft ? 0 : (int) mTabPadding, 0, (int) mTabPadding, 0);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tv_tab_title.getLayoutParams();
                switch (mTextGravity) {
                    case TEXT_GRAVITY_CENTER_HORIZONTAL_WITH_BOTTOM:
                        layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                        layoutParams.bottomMargin = mTextMarginBottom;
                        break;
                    case TEXT_GRAVITY_CENTER:
                    default:
                        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                        layoutParams.bottomMargin = 0;
                        break;
                }

            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        /*
         * position:当前View的位置
         * mCurrentPositionOffset:当前View的偏移量比例.[0,1)
         */
        this.mCurrentTab = position;
        this.mCurrentPositionOffset = positionOffset;
        scrollToCurrentTab();
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        updateTabSelection(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * HorizontalScrollView滚到当前tab,并且居中显示
     */
    private void scrollToCurrentTab() {
        if (mTabCount <= 0 || mTabsContainer.getChildAt(mCurrentTab) == null) {
            return;
        }

        int offset = (int) (mCurrentPositionOffset * mTabsContainer.getChildAt(mCurrentTab).getWidth());
        /*当前Tab的left+当前Tab的Width乘以positionOffset*/
        int newScrollX = mTabsContainer.getChildAt(mCurrentTab).getLeft() + offset;

        if (mCurrentTab > 0 || offset > 0) {
            /*HorizontalScrollView移动到当前tab,并居中*/
            newScrollX -= getWidth() / 2 + getPaddingLeft();
            calcIndicatorRect();
            newScrollX += ((mTabRect.right - mTabRect.left) / 2);
        }

        if (newScrollX != mLastScrollX) {
            mLastScrollX = newScrollX;
            /* scrollTo（int x,int y）:x,y代表的不是坐标点,而是偏移量
             *  x:表示离起始位置的x水平方向的偏移量
             *  y:表示离起始位置的y垂直方向的偏移量
             */
            scrollTo(newScrollX, 0);
        }
    }

    public void updateTabSelection(int position) {
        for (int i = 0; i < mTabCount; ++i) {
            View tabView = mTabsContainer.getChildAt(i);
            final boolean isSelect = i == position;
            TextView tab_title = tabView.findViewById(R.id.tv_tab_title);
            if (tab_title != null) {
                if (mDrawableLeftArray.get(i) <= 0) {
                    tabView.setBackground(isSelect ? mTabSelectDrawable : mTabUnSelectDrawable);
                    tab_title.setTextColor(isSelect ? mTextSelectColor : mTextUnselectColor);
                    tab_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, isSelect || mUnSelectTextSize <= 0 ? mTextsize : mUnSelectTextSize);
                    if (mTextBold == TEXT_BOLD_WHEN_SELECT) {
                        tab_title.getPaint().setFakeBoldText(isSelect);
                        tab_title.postInvalidate();
                    }
                } else {
                    tab_title.setCompoundDrawablesWithIntrinsicBounds(isSelect ? mDrawableLeftArraySelected.get(i) : mDrawableLeftArray.get(i), 0, 0, 0);
                }
            }
            setMsgMargin(position, i);
        }
    }

    private float margin;

    private void calcIndicatorRect() {
        View currentTabView = mTabsContainer.getChildAt(this.mCurrentTab);
        float left = currentTabView.getLeft();
        float right = currentTabView.getRight();

        //for mIndicatorWidthEqualTitle
        if (mIndicatorStyle == STYLE_NORMAL && mIndicatorWidthEqualTitle) {
            TextView tab_title = currentTabView.findViewById(R.id.tv_tab_title);
            mTextPaint.setTextSize(mTextsize);
            float textWidth = mTextPaint.measureText(tab_title.getText().toString());
            margin = (right - left - textWidth) / 2;
        }

        if (this.mCurrentTab < mTabCount - 1) {
            View nextTabView = mTabsContainer.getChildAt(this.mCurrentTab + 1);
            float nextTabLeft = nextTabView.getLeft();
            float nextTabRight = nextTabView.getRight();

            left = left + mCurrentPositionOffset * (nextTabLeft - left);
            right = right + mCurrentPositionOffset * (nextTabRight - right);

            //for mIndicatorWidthEqualTitle
            if (mIndicatorStyle == STYLE_NORMAL && mIndicatorWidthEqualTitle) {
                TextView next_tab_title = nextTabView.findViewById(R.id.tv_tab_title);
                mTextPaint.setTextSize(mTextsize);
                float nextTextWidth = mTextPaint.measureText(next_tab_title.getText().toString());
                float nextMargin = (nextTabRight - nextTabLeft - nextTextWidth) / 2;
                margin = margin + mCurrentPositionOffset * (nextMargin - margin);
            }
        }

        mIndicatorRect.left = (int) left;
        mIndicatorRect.right = (int) right;
        //for mIndicatorWidthEqualTitle
        if (mIndicatorStyle == STYLE_NORMAL && mIndicatorWidthEqualTitle) {
            mIndicatorRect.left = (int) (left + margin - 1);
            mIndicatorRect.right = (int) (right - margin - 1);
        }

        mTabRect.left = (int) left;
        mTabRect.right = (int) right;

        if (!mIndicatorWidthEqualTitle && mIndicatorWidth >= 0) {//indicatorWidth大于0时,圆角矩形以及三角形
            float indicatorLeft = currentTabView.getLeft() + (currentTabView.getWidth() - mIndicatorWidth) / 2;

            if (this.mCurrentTab < mTabCount - 1) {
                View nextTab = mTabsContainer.getChildAt(this.mCurrentTab + 1);
                indicatorLeft = indicatorLeft + mCurrentPositionOffset * (currentTabView.getWidth() / 2f + nextTab.getWidth() / 2f);
            }

            mIndicatorRect.left = (int) indicatorLeft;
            mIndicatorRect.right = (int) (mIndicatorRect.left + mIndicatorWidth);
        }
        //indicatorWidth小于0时,原jpardogo's PagerSlidingTabStrip
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || mTabCount <= 0) {
            return;
        }

        int height = getHeight();
        int paddingLeft = getPaddingLeft();
        // draw divider
        if (mDividerWidth > 0) {
            mDividerPaint.setStrokeWidth(mDividerWidth);
            mDividerPaint.setColor(mDividerColor);
            for (int i = 0; i < mTabCount - 1; i++) {
                View tab = mTabsContainer.getChildAt(i);
                canvas.drawLine(paddingLeft + tab.getRight(), mDividerPadding, paddingLeft + tab.getRight(), height - mDividerPadding, mDividerPaint);
            }
        }

        // draw underline
        if (mUnderlineHeight > 0) {
            mRectPaint.setColor(mUnderlineColor);
            if (mUnderlineGravity == Gravity.BOTTOM) {
                canvas.drawRect(paddingLeft, height - mUnderlineHeight, mTabsContainer.getWidth() + paddingLeft, height, mRectPaint);
            } else {
                canvas.drawRect(paddingLeft, 0, mTabsContainer.getWidth() + paddingLeft, mUnderlineHeight, mRectPaint);
            }
        }

        //draw indicator line

        calcIndicatorRect();
        if (mIndicatorStyle == STYLE_TRIANGLE) {
            if (mIndicatorHeight > 0) {
                mTrianglePaint.setColor(mIndicatorColor);
                mTrianglePath.reset();
                mTrianglePath.moveTo(paddingLeft + mIndicatorRect.left, height);
                mTrianglePath.lineTo(paddingLeft + mIndicatorRect.left / 2f + mIndicatorRect.right / 2f, height - mIndicatorHeight);
                mTrianglePath.lineTo(paddingLeft + mIndicatorRect.right, height);
                mTrianglePath.close();
                canvas.drawPath(mTrianglePath, mTrianglePaint);
            }
        } else if (mIndicatorStyle == STYLE_BLOCK) {
            if (mIndicatorHeight < 0) {
                mIndicatorHeight = height - mIndicatorMarginTop - mIndicatorMarginBottom;
            }

            if (mIndicatorHeight > 0) {
                if (mIndicatorCornerRadius < 0 || mIndicatorCornerRadius > mIndicatorHeight / 2) {
                    mIndicatorCornerRadius = mIndicatorHeight / 2;
                }
                mIndicatorDrawable.setColor(mIndicatorColor);
                mIndicatorDrawable.setBounds(paddingLeft + (int) mIndicatorMarginLeft + mIndicatorRect.left,
                        (int) mIndicatorMarginTop, (int) (paddingLeft + mIndicatorRect.right - mIndicatorMarginRight),
                        (int) (mIndicatorMarginTop + mIndicatorHeight));
                mIndicatorDrawable.setCornerRadius(mIndicatorCornerRadius);
                mIndicatorDrawable.draw(canvas);
            }
        } else {
            if (mIndicatorHeight > 0) {
                if (mIndicatorImgDrawable != null) { //只适配了在底部的
                    int indicatorWidth = mIndicatorImgDrawable.getIntrinsicWidth();
                    int indicatorHeight = mIndicatorImgDrawable.getIntrinsicHeight();
                    int left = paddingLeft + (int) mIndicatorMarginLeft + mIndicatorRect.left;
                    int right = paddingLeft + mIndicatorRect.right - (int) mIndicatorMarginRight;
                    int tabWidth = right - left;
                    left += (tabWidth - indicatorWidth) / 2;
                    right -= (tabWidth - indicatorWidth) / 2;
                    mIndicatorImgDrawable.setBounds(left,
                            height - indicatorHeight - (int) mIndicatorMarginBottom,
                            right,
                            height - (int) mIndicatorMarginBottom);
                    mIndicatorImgDrawable.draw(canvas);
                } else {
                    if (mIndicatorColorList == null)
                        mIndicatorDrawable.setColor(mIndicatorColor);
                    else if (mIndicatorColorList.length == 1)
                        mIndicatorDrawable.setColor(mIndicatorColorList[0]);
                    if (mIndicatorGravity == Gravity.BOTTOM) {
                        mIndicatorDrawable.setBounds(paddingLeft + (int) mIndicatorMarginLeft + mIndicatorRect.left,
                                height - (int) mIndicatorHeight - (int) mIndicatorMarginBottom,
                                paddingLeft + mIndicatorRect.right - (int) mIndicatorMarginRight,
                                height - (int) mIndicatorMarginBottom);
                    } else {
                        mIndicatorDrawable.setBounds(paddingLeft + (int) mIndicatorMarginLeft + mIndicatorRect.left,
                                (int) mIndicatorMarginTop,
                                paddingLeft + mIndicatorRect.right - (int) mIndicatorMarginRight,
                                (int) mIndicatorHeight + (int) mIndicatorMarginTop);
                    }
                    mIndicatorDrawable.setCornerRadius(mIndicatorCornerRadius);
                    mIndicatorDrawable.draw(canvas);
                }
            }
        }
    }

    // region setter and getter
    public void setTextMarginBottom(int textMarginBottom) {
        mTextMarginBottom = textMarginBottom;
    }

    public void setIndicatorImgDrawable(Drawable indicatorDrawable) {
        mIndicatorImgDrawable = indicatorDrawable;
    }

    public void setIndicatorColorList(int[] indicatorColorList) {
        mIndicatorColorList = indicatorColorList;
        if (indicatorColorList != null && indicatorColorList.length > 1)
            mIndicatorDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, indicatorColorList);
        else
            mIndicatorDrawable = new GradientDrawable();
    }

    public void setNeedRemoveFirstItemPaddingLeft(boolean needRemoveFirstItemPaddingLeft) {
        this.needRemoveFirstItemPaddingLeft = needRemoveFirstItemPaddingLeft;
    }

    public void setTabMarginLeft(int tabMarginLeft) {
        mTabMarginLeft = tabMarginLeft;
    }

    /**
     * 目前仅支持以下值，其它形式待添加支持
     * {@link SlidingTabLayout#TEXT_GRAVITY_CENTER}
     * {@link SlidingTabLayout#TEXT_GRAVITY_CENTER_HORIZONTAL_WITH_BOTTOM}
     */
    public void setTextGravity(byte textGravity) {
        mTextGravity = textGravity;
    }

    public void setTabSelectDrawable(Drawable tabSelectDrawable) {
        mTabSelectDrawable = tabSelectDrawable;
    }

    public void setDrawableLeftResId(int position, int drawableLeftResId, int drawableLeftSelectedResId) {
        if (drawableLeftResId < 0) drawableLeftResId = 0;
        if (drawableLeftSelectedResId < 0) drawableLeftSelectedResId = 0;
        mDrawableLeftArray.put(position, drawableLeftResId);
        mDrawableLeftArraySelected.put(position, drawableLeftSelectedResId);
        if (mTitles == null && (mViewPager == null || mViewPager.getAdapter() == null)) return;

        CharSequence pageTitle = mTitles == null ? mViewPager.getAdapter().getPageTitle(position) : mTitles.get(position);
        if (mTabsContainer != null && mTabsContainer.getChildCount() > position) {
            boolean isSelect = mCurrentTab == position;
            View tabView = mTabsContainer.getChildAt(position);
            TextView tab_title = tabView.findViewById(R.id.tv_tab_title);
            tab_title.setCompoundDrawablesWithIntrinsicBounds(isSelect ? drawableLeftSelectedResId : drawableLeftResId, 0, 0, 0);
            if (drawableLeftResId > 0) {
                tab_title.setText("");
            } else {
                tab_title.setText(pageTitle);

                tabView.setBackground(isSelect ? mTabSelectDrawable : mTabUnSelectDrawable);
                tab_title.setTextColor(isSelect ? mTextSelectColor : mTextUnselectColor);
                tab_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, isSelect || mUnSelectTextSize <= 0 ? mTextsize : mUnSelectTextSize);
                if (mTextBold == TEXT_BOLD_WHEN_SELECT) {
                    tab_title.getPaint().setFakeBoldText(isSelect);
                }
                setMsgMargin(position, position);
            }
        }
    }

    public void setTabUnSelectDrawable(Drawable tabUnSelectDrawable) {
        mTabUnSelectDrawable = tabUnSelectDrawable;
    }

    //sp
    public void setUnSelectTextSize(float unSelectTextSizeSp) {
        mUnSelectTextSize = sp2px(unSelectTextSizeSp);
    }

    public void setTipViewPadding(int msgViewPadding) {
        mTipViewPadding = msgViewPadding;
    }

    public void setTipViewHeight(int tipViewHeight) {
        mTipViewHeight = tipViewHeight;
    }

    public void setCurrentTab(int currentTab) {
        setCurrentTab(currentTab, false);
    }

    public void setCurrentTab(int currentTab, boolean smoothScroll) {
        setCurrentTab(currentTab, smoothScroll, true);
    }

    /**
     * 设置当前选项
     *
     * @param currentTab   当前选项
     * @param smoothScroll 是否带滑动效果
     * @param callListener 是否触发相应监听----否则只更新Tab索引栏，不进行通知ViewPager及其它相应监听器
     */
    public void setCurrentTab(int currentTab, boolean smoothScroll, boolean callListener) {
        if (callListener) {
            if (mViewPager != null) {
                this.mCurrentTab = currentTab;
                mViewPager.setCurrentItem(currentTab, smoothScroll);
            } else {
                if (mCurrentTab == currentTab) {
                    if (mListener != null) {
                        mListener.onTabReselect(currentTab);
                    }
                } else {
                    mCurrentTab = currentTab;
                    scrollToCurrentTab();
                    updateTabSelection(currentTab);
                    if (mListener != null) {
                        mListener.onTabSelect(currentTab);
                    }
                }
            }
        } else {
            if (mCurrentTab != currentTab) {
                mCurrentTab = currentTab;
                scrollToCurrentTab();
                updateTabSelection(currentTab);
            }
        }
    }

    public void setIndicatorStyle(int indicatorStyle) {
        this.mIndicatorStyle = indicatorStyle;
        invalidate();
    }

    public void setDotSize(int dotSize) {
        mDotSize = dotSize;
    }

    public void setTipBackgroundColor(int tipBackgroundColor) {
        mTipBackgroundColor = tipBackgroundColor;
    }

    public void setTipTextColor(int tipTextColor) {
        mTipTextColor = tipTextColor;
    }

    public void setTipBackgroundDrawable(Drawable tipBackgroundDrawable, int tipIconSize) {
        mTipBackgroundDrawable = tipBackgroundDrawable;
        mTipIconSize = tipIconSize;
    }

    public void setTipIconTopMargin(int tipIconTopMargin) {
        mTipIconTopMargin = tipIconTopMargin;
    }

    //单位sp
    public void setTipTextSize(int textSizeSp) {
        mTipTextSize = textSizeSp;
    }

    public void setTipMinWidth(int tipMinWidth) {
        mTipMinWidth = tipMinWidth;
    }

    public void setTipMarginTop(int tipMarginTop) {
        mTipMarginTop = tipMarginTop;
    }

    public void setTipMarginLeft(int tipMarginLeft) {
        mTipMarginLeft = tipMarginLeft;
    }

    public void setTabPadding(float tabPadding) {
        this.mTabPadding = dp2px(tabPadding);
        updateTabStyles();
    }

    public void setTabSpaceEqual(boolean tabSpaceEqual) {
        this.mTabSpaceEqual = tabSpaceEqual;
        this.mTabPadding = 0;
        updateTabStyles();
    }

    public void setTabWidth(float tabWidth) {
        this.mTabWidth = dp2px(tabWidth);
        this.mTabPadding = 0;
        updateTabStyles();
    }

    public void setTabWidthPx(float tabWidth) {
        this.mTabWidth = tabWidth;
        this.mTabPadding = 0;
        updateTabStyles();
    }

    public void setIndicatorColor(int indicatorColor) {
        this.mIndicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorHeight(float indicatorHeight) {
        this.mIndicatorHeight = dp2px(indicatorHeight);
        invalidate();
    }

    public void setIndicatorWidth(float indicatorWidth) {
        this.mIndicatorWidth = dp2px(indicatorWidth);
        invalidate();
    }

    public void setIndicatorCornerRadius(float indicatorCornerRadius) {
        this.mIndicatorCornerRadius = dp2px(indicatorCornerRadius);
        invalidate();
    }

    public void setIndicatorGravity(int indicatorGravity) {
        this.mIndicatorGravity = indicatorGravity;
        invalidate();
    }

    public void setIndicatorMargin(float indicatorMarginLeft, float indicatorMarginTop,
                                   float indicatorMarginRight, float indicatorMarginBottom) {
        this.mIndicatorMarginLeft = dp2px(indicatorMarginLeft);
        this.mIndicatorMarginTop = dp2px(indicatorMarginTop);
        this.mIndicatorMarginRight = dp2px(indicatorMarginRight);
        this.mIndicatorMarginBottom = dp2px(indicatorMarginBottom);
        invalidate();
    }

    public void setIndicatorWidthEqualTitle(boolean indicatorWidthEqualTitle) {
        this.mIndicatorWidthEqualTitle = indicatorWidthEqualTitle;
        invalidate();
    }

    public void setUnderlineColor(int underlineColor) {
        this.mUnderlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineHeight(float underlineHeight) {
        this.mUnderlineHeight = dp2px(underlineHeight);
        invalidate();
    }

    public void setUnderlineGravity(int underlineGravity) {
        this.mUnderlineGravity = underlineGravity;
        invalidate();
    }

    public void setDividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
        invalidate();
    }

    public void setDividerWidth(float dividerWidth) {
        this.mDividerWidth = dp2px(dividerWidth);
        invalidate();
    }

    public void setDividerPadding(float dividerPadding) {
        this.mDividerPadding = dp2px(dividerPadding);
        invalidate();
    }

    public void setTextsize(float textsize) {
        this.mTextsize = sp2px(textsize);
        updateTabStyles();
    }

    public void setTextSelectColor(int textSelectColor) {
        this.mTextSelectColor = textSelectColor;
        updateTabStyles();
    }

    public void setTextUnselectColor(int textUnSelectColor) {
        this.mTextUnselectColor = textUnSelectColor;
        updateTabStyles();
    }

    public void setTextBold(int textBold) {
        this.mTextBold = textBold;
        updateTabStyles();
    }

    public void setTextAllCaps(boolean textAllCaps) {
        this.mTextAllCaps = textAllCaps;
        updateTabStyles();
    }

    public void setSnapOnTabClick(boolean snapOnTabClick) {
        mSnapOnTabClick = snapOnTabClick;
    }

    public int getTabCount() {
        return mTabCount;
    }

    public int getCurrentTab() {
        return mCurrentTab;
    }
    // endregion

    // region show Tip
    public void showTipIcon(int position) {
        if (position >= mTabCount)
            position = mTabCount - 1;

        View tabView = mTabsContainer.getChildAt(position);
        if (mTipBackgroundDrawable != null && mTipIconSize > 0) {
            View TipIconView = new View(getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mTipIconSize, mTipIconSize);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            layoutParams.topMargin = mTipIconTopMargin;
            TipIconView.setLayoutParams(layoutParams);
            TipIconView.setBackground(mTipBackgroundDrawable);
            ((RelativeLayout) tabView).addView(TipIconView);
        }
    }

    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final SparseBooleanArray mInitSetMap = new SparseBooleanArray();

    /**
     * 显示未读消息
     *
     * @param position 显示tab位置
     * @param num      num小于等于0显示红点,num大于0显示数字
     */
    public void showMsg(int position, int num) {
        if (position >= mTabCount)
            position = mTabCount - 1;

        View tabView = mTabsContainer.getChildAt(position);
        MsgView tipView = tabView.findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            if (mTipBackgroundColor != -1)
                tipView.setBackgroundColor(mTipBackgroundColor);
            if (mTipTextSize > 0)
                tipView.setTextSize(mTipTextSize);
            if (mTipTextColor != 0)
                tipView.setTextColor(mTipTextColor);
            if (mTipMinWidth != -1) {
                tipView.setMinimumWidth(mTipMinWidth);
                tipView.setMinWidth(mTipMinWidth);
            }
            if (mTipViewPadding != -1)
                tipView.setPadding(mTipViewPadding, 0, mTipViewPadding, 0);
            if (mTipViewHeight > 0) {
                if (mTipMinWidth == -1)
                    tipView.setMinWidth(mTipViewHeight);
                MarginLayoutParams lp = (MarginLayoutParams) tipView.getLayoutParams();
                lp.height = mTipViewHeight;
                tipView.setLayoutParams(lp);
            }

            show(tipView, num);
            if (num <= 0 && mDotSize > 0) { //圆点则进行修改大小，宽高为18的圆点
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tipView.getLayoutParams();
                lp.width = mDotSize;
                lp.height = mDotSize;
                tipView.setLayoutParams(lp);
            }
            if (mInitSetMap.get(position)) return;

            setMsgMargin(mCurrentTab, position);
            mInitSetMap.put(position, true);
        }
    }

    private static void show(MsgView msgView, int num) {
        if (msgView == null) {
            return;
        }
        msgView.setVisibility(View.VISIBLE);
        if (num <= 0) {//圆点,设置默认宽高
            msgView.setStrokeWidth(0);
            msgView.setText("");
        } else {
            if (num < 10) {//圆
                msgView.setText(String.valueOf(num));
            } else if (num < 100) {//圆角矩形,圆角是高度的一半,设置默认padding
                msgView.setText(String.valueOf(num));
            } else {//数字超过两位,显示99+
                msgView.setText("99+");
            }
        }
    }

    /**
     * 显示未读红点
     *
     * @param position 显示tab位置
     */
    public void showDot(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        showMsg(position, 0);
    }

    /**
     * 隐藏未读消息
     */
    public void hideMsg(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }

        View tabView = mTabsContainer.getChildAt(position);
        MsgView tipView = tabView.findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            tipView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置未读消息偏移,原点为文字的右上角.当控件高度固定,消息提示位置易控制,显示效果佳
     */
    private void setMsgMargin(int selectPosition, int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        View tabView = mTabsContainer.getChildAt(position);
        MsgView tipView = tabView.findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            TextView tv_tab_title = tabView.findViewById(R.id.tv_tab_title);
            mTextPaint.setTextSize(selectPosition == position ? mTextsize : (mUnSelectTextSize > 0 ? mUnSelectTextSize : mTextsize));
            float textWidth = mTextPaint.measureText(tv_tab_title.getText().toString());
            float textHeight = mTextPaint.descent() - mTextPaint.ascent();
            MarginLayoutParams lp = (MarginLayoutParams) tipView.getLayoutParams();
            lp.leftMargin = mTabWidth >= 0 ? (int) (mTabWidth / 2 + textWidth / 2 + mTipMarginLeft) : (int) (mTabPadding + textWidth + mTipMarginLeft);
            //当lp.topMargin = 0;的时候，红点会莫名向上偏移几个像素，所以都进行加2处理
            if (mTipMarginTop == -1)
                lp.topMargin = 2;
            else if (mTipMarginTop > 0)
                lp.topMargin = mTipMarginTop + 2;
            else if (mHeight > 0)
                lp.topMargin = (int) (mHeight - textHeight) / 2 + 2;
            else
                lp.topMargin = 2;
            lp.bottomMargin = lp.rightMargin = 0;
            tipView.setLayoutParams(lp);
        }
    }
    // endregion

    private OnTabSelectListener mListener;

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        this.mListener = listener;
    }

    static class InnerPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> fragments;
        private final String[] titles;

        InnerPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, String[] titles) {
            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            // 覆写destroyItem并且空实现,这样每个Fragment中的视图就不会被销毁
            // super.destroyItem(container, position, object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("mCurrentTab", mCurrentTab);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCurrentTab = bundle.getInt("mCurrentTab");
            state = bundle.getParcelable("instanceState");
            if (mCurrentTab != 0 && mTabsContainer.getChildCount() > 0) {
                updateTabSelection(mCurrentTab);
                scrollToCurrentTab();
            }
        }
        super.onRestoreInstanceState(state);
    }

    protected int dp2px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    protected int sp2px(float sp) {
            final float scale = this.mContext.getResources().getDisplayMetrics().scaledDensity;
            return (int) (sp * scale + 0.5f);
    }

    public interface OnTabSelectListener {
        void onTabSelect(int position);
        void onTabReselect(int position);
    }
}
