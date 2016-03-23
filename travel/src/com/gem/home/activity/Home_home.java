package com.gem.home.activity;

//首页
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gem.home.action.Renovate;
import com.gem.home.dao.MyRecyclerViewAdapter;
import com.gem.home.dao.MyViewPagerAdapter;
import com.gem.home.dao.OnItemClickLitener;
import com.gem.home.until.PublishTravel;
import com.gem.home.view.SpaceItem;
import com.gem.home.view.ViewPagerIndicator;
import com.gem.home.view.ViewPagerIndicator.PageOnChangeListener;
import com.gem.scenery.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Home_home extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
	private ViewPagerIndicator mIndicator;
	private ViewPager mViewPager;
	private View ah_Newsest, ah_Aimplace, ah_Hot, ah_Shuttime;
	// viewpager下四个动态页面
	private List<View> mViewList;
	private List<String> mTitleList, mDatas, userNamelist;
	private RecyclerView mRecyclerView, news_RecyclerView, aim_RecyclerView, shut_RecyclerView;
	private SwipeRefreshLayout mSwiperefreshlayout;
	private MyRecyclerViewAdapter myRecyclerViewAdapter;

	private static final int REFRESH_COMPLETE = 0X110;
	private Context context;
	private List<PublishTravel> arr = new ArrayList<PublishTravel>();
	private Renovate mRenovate;
	private MyViewPagerAdapter mViewPagerAdapter;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Log.i("refresh", "handlemessage");
			switch (msg.what) {
			case REFRESH_COMPLETE:
				myRecyclerViewAdapter.notifyDataSetChanged();
				mSwiperefreshlayout.setRefreshing(false);
				break;

			}
		};
	};

	// 初始化加载fragment里的部件
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_home, container, false);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getContext();
		// 初始化控件加载布局
		inView(savedInstanceState);
		// 初始化数据
		initData();
		// 设置recyclerview排版样式（这里是垂直排列）
		mRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayout.VERTICAL, false));
		news_RecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayout.VERTICAL, false));
		aim_RecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayout.VERTICAL, false));
		shut_RecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayout.VERTICAL, false));
		// recycler中 item空隙及样式
		int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
		mRecyclerView.addItemDecoration(new SpaceItem(spacingInPixels));
		news_RecyclerView.addItemDecoration(new SpaceItem(spacingInPixels));
		aim_RecyclerView.addItemDecoration(new SpaceItem(spacingInPixels));
		shut_RecyclerView.addItemDecoration(new SpaceItem(spacingInPixels));

		// recycler适配器
		myRecyclerViewAdapter = new MyRecyclerViewAdapter(arr, context);
		// 网络通讯初始化首页数据
		mRenovate = new Renovate();
		mRenovate.initData(myRecyclerViewAdapter, arr,0);
		// recycler中item监听事件
		myRecyclerViewAdapter.setOnItemClickListener(new OnItemClickLitener() {

			@Override
			public void onItemClick(View view, int position) {
				Toast.makeText(context, position + " click", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(context, Item_Activity.class);
				context.startActivity(intent);

			}
		});

		mRecyclerView.setAdapter(myRecyclerViewAdapter);
		news_RecyclerView.setAdapter(myRecyclerViewAdapter);
		aim_RecyclerView.setAdapter(myRecyclerViewAdapter);
		shut_RecyclerView.setAdapter(myRecyclerViewAdapter);
		// 下拉刷新控件
		mSwiperefreshlayout = (SwipeRefreshLayout) ah_Hot.findViewById(R.id.hot_swiperefreshlayout);
		mSwiperefreshlayout.setOnRefreshListener(this);
		mSwiperefreshlayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);// 刷新控件颜色定义
		mViewPager.setAdapter(mViewPagerAdapter);
		mIndicator.setTabItemTitles(mTitleList);
		mIndicator.setVisibleTabCount(4);
		mIndicator.setViewPager(mViewPager, 0);
		mIndicator.setOnPageChangeListener(new PageOnChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				// viewpager页面改变时请求新数据
				
				mRenovate.initData(myRecyclerViewAdapter, arr,arg0);
				Log.i("onPageSelected", "arg0+" + arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				Log.i("onPageScrolled", "arg0+" + arg0);
				Log.i("onPageScrolled", "arg1+" + arg1);
				Log.i("onPageScrolled", "arg2+" + arg2);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				Log.i("onPageScrollStateChanged", "arg0+" + arg0);
			}
		});

	}

	// 初始化控件
	private void inView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mViewPager = (ViewPager) this.getView().findViewById(R.id.home_viewpager);
		mIndicator = (ViewPagerIndicator) this.getView().findViewById(R.id.id_indicator);
		// 找到viewpager和滑动标题
		// 四个滑动页面
		ah_Hot = this.getView().findViewById(R.layout.ah_hot);
		ah_Newsest = this.getView().findViewById(R.layout.ah_newsest);
		ah_Aimplace = this.getView().findViewById(R.layout.ah_aimplace);
		ah_Shuttime = this.getView().findViewById(R.layout.ah_shuttime);
		// 加载布局

		LayoutInflater lf = getLayoutInflater(savedInstanceState).from(context);
		ah_Hot = lf.inflate(R.layout.ah_hot, null);
		ah_Newsest = lf.inflate(R.layout.ah_newsest, null);
		ah_Aimplace = lf.inflate(R.layout.ah_aimplace, null);
		ah_Shuttime = lf.inflate(R.layout.ah_shuttime, null);
		// 加载recyleview
		mRecyclerView = (RecyclerView) ah_Hot.findViewById(R.id.hot_recyclerview);
		news_RecyclerView = (RecyclerView) ah_Newsest.findViewById(R.id.new_recyclerview);
		aim_RecyclerView = (RecyclerView) ah_Aimplace.findViewById(R.id.aim_recyclerview);
		shut_RecyclerView = (RecyclerView) ah_Shuttime.findViewById(R.id.shut_recyclerview);

	}

	@Override
	public void onRefresh() { // TODO Auto-generated method stub //
		mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
		// 下拉刷新
		// mRenovate.renvoate();
		// mDatas.addAll(Arrays.asList("Lucene", "Canvas", "Bitmap"));
		// userNamelist.addAll(Arrays.asList("jack", "tony", "mike"));
		Log.i("refresh", "onrefresh");
	}

	private void initData() {
		// TODO Auto-generated method stub
		// 初始化item里的数据
		// mDatas = new ArrayList<String>();
		// userNamelist = new ArrayList<String>();
		// userNamelist.add("tim");
		// userNamelist.add("milo");
		// mDatas.add("java");
		// mDatas.add("android");

		// 布局加入
		mViewList = new ArrayList<View>();
		mViewList.add(ah_Hot);
		mViewList.add(ah_Newsest);
		mViewList.add(ah_Aimplace);
		mViewList.add(ah_Shuttime);

		mTitleList = new ArrayList<String>();
		mTitleList.add("热门");
		mTitleList.add("最新");
		mTitleList.add("目的地");
		mTitleList.add("结束时间");

		// viewpager适配器

		mViewPagerAdapter = new MyViewPagerAdapter(mViewList);
	}
}