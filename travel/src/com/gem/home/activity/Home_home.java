package com.gem.home.activity;

//首页
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gem.home.action.Renovate;
import com.gem.home.dao.MyApplication;
import com.gem.home.dao.MyImageAsyncTask;
import com.gem.home.dao.MyRecyclerViewAdapter;
import com.gem.home.dao.MyRecyclerViewHolder;
import com.gem.home.dao.MyViewPagerAdapter;
import com.gem.home.dao.OnItemClickLitener;
import com.gem.home.until.PublishTravel;
import com.gem.home.view.SpaceItem;
import com.gem.home.view.ViewPagerIndicator;
import com.gem.home.view.ViewPagerIndicator.PageOnChangeListener;
import com.gem.scenery.R;
import com.gem.scenery.entity.PersonalData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Home_home extends Fragment {
	private ViewPagerIndicator mIndicator;// 自定义ViewPage指示器
	private ViewPager mViewPager;
	private View ah_Newsest, ah_Aimplace, ah_Hot, ah_Shuttime;// viewpager下四个动态页面
	private List<View> mViewList;// 存放四个布局的
	private List<String> mTitleList;// 存放四个标题的，
	private RecyclerView mRecyclerView, news_RecyclerView, aim_RecyclerView, shut_RecyclerView;
	private SwipeRefreshLayout mSwiperefreshlayout, news_Swiperefreshlayout, aim_Swiperefreshlayout,
			shut_Swiperefreshlayout;
	private MyRecyclerViewAdapter myRecyclerViewAdapter;
	private int lastVisibleItem;// 最后可见的Item
	private static final int MORE_ITEM = 2;// 表示上拉加载
	private static final int REFRESH_COMPLETE = 1;// 表示下拉刷新
	private static final int NEW_REFRESH_COMPLETE = 10;
	private static final int NEW_MORE_ITEM = 20;
	private static final int AIM_REFRESH_COMPLETE = 100;
	private static final int AIM_MORE_ITEM = 200;
	private static final int SHUT_REFRESH_COMPLETE = 1000;
	private static final int SHUT_MORE_ITEM = 2000;
	private int Currment = 0;// 表示的是那一页（即哪个viewPage）
	private int nowP = REFRESH_COMPLETE;;
	private int nextP = 0;
	private Context context;
	private List<PublishTravel> arr = new ArrayList<PublishTravel>();
	private Renovate mRenovate;
	private MyViewPagerAdapter mViewPagerAdapter;
	private MyApplication maApplication;
	int i = 0;
	private static boolean flag = false;

	// int j=1;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Log.i("refresh", "handlemessage");
			switch (msg.what) {
			case REFRESH_COMPLETE:
				// 热门下拉刷新
				nextP = 0;
				mRenovate.initData(myRecyclerViewAdapter, arr, Currment, REFRESH_COMPLETE);
				mRecyclerView.setAdapter(myRecyclerViewAdapter);
				mSwiperefreshlayout.setRefreshing(false);
				break;
			case MORE_ITEM:
				// 上拉加载，加载数据源未实现
				// mRenovate.loadData(myRecyclerViewAdapter, arr, Currment,
				// REFRESH_COMPLETE);
				if (mRenovate != null) {
					if (flag) {
						myRecyclerViewAdapter.notifyDataSetChanged();
					}
				}
				mSwiperefreshlayout.setRefreshing(false);
				break;
			case NEW_REFRESH_COMPLETE:
				// 最新下拉刷新
				nextP = 0;
				mRenovate.initData(myRecyclerViewAdapter, arr, Currment, REFRESH_COMPLETE);
				news_RecyclerView.setAdapter(myRecyclerViewAdapter);
				news_Swiperefreshlayout.setRefreshing(false);
				break;
			case NEW_MORE_ITEM:
				if (mRenovate != null) {
					if (flag) {
						myRecyclerViewAdapter.notifyDataSetChanged();
					}
				}
				news_Swiperefreshlayout.setRefreshing(false);
				break;
			case AIM_REFRESH_COMPLETE:
				// AIM下拉刷新
				nextP = 0;
				mRenovate.initData(myRecyclerViewAdapter, arr, Currment, REFRESH_COMPLETE);
				aim_RecyclerView.setAdapter(myRecyclerViewAdapter);
				aim_Swiperefreshlayout.setRefreshing(false);
				break;
			case AIM_MORE_ITEM:
				if (mRenovate != null) {
					if (flag) {
						myRecyclerViewAdapter.notifyDataSetChanged();
					}
				}
				aim_Swiperefreshlayout.setRefreshing(false);
				break;
			case SHUT_REFRESH_COMPLETE:
				// SHUT下拉刷新
				nextP = 0;
				mRenovate.initData(myRecyclerViewAdapter, arr, Currment, REFRESH_COMPLETE);
				shut_RecyclerView.setAdapter(myRecyclerViewAdapter);
				shut_Swiperefreshlayout.setRefreshing(false);
				break;
			case SHUT_MORE_ITEM:
				if (mRenovate != null) {
					if (flag) {
						myRecyclerViewAdapter.notifyDataSetChanged();
					}
				}
				shut_Swiperefreshlayout.setRefreshing(false);
				break;
			default:
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

	/**
	 * 请求网络访问多级评论
	 */
	String url = "http://10.201.1.12:8080/travel/TravelComment";

	public void sendContent(final PublishTravel pt, final int position, final PersonalData pd) {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("td", String.valueOf(pt.getTd()));
		// params.addBodyParameter("ld",String.valueOf(maApplication.getLd().getLd()));
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "请求失败，请检查网络", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(context, Item_Activity.class);
				intent.putExtra("flag", false);

				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
				intent.putExtra("pt", gson.toJson(pt));// 旅行队
				context.startActivity(intent);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				String result = arg0.result;
				if (result != null) {
					Intent intent = new Intent(context, Item_Activity.class);
					Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
					intent.putExtra("flag", true);
					intent.putExtra("pt", gson.toJson(pt));// 旅行队
					intent.putExtra("content", result);// 旅行队评论
					intent.putExtra("pd", pd);
					context.startActivity(intent);
				}
			}
		});
	}

	String urlU = "http://10.201.1.12:8080/travel/Home_home_yhtx";

	/**
	 * 获取用户图片
	 */
	public void sendUserPicture(final PublishTravel pt, final int position) {
		RequestParams params = new RequestParams();
		HttpUtils http = new HttpUtils();
		params.addBodyParameter("ld", String.valueOf(pt.getLd().getLd()));
		http.send(HttpMethod.POST, urlU, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "请求失败，请检查网络", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				String result = arg0.result;
				if (!result.equals(null) && result != null) {
					Gson gson = new Gson();
					Type type = new TypeToken<PersonalData>() {
					}.getType();
					PersonalData pd = gson.fromJson(result, type);
					sendContent(pt, position, pd);
				}
			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getContext();
		maApplication = (MyApplication) context.getApplicationContext();
		// 初始化控件加载布局
		inView(savedInstanceState);
		// 初始化数据
		initData();
		// 设置recyclerview排版样式（这里是垂直排列）
		final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
		mRecyclerView.setLayoutManager(linearLayoutManager);
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

		mRenovate.initData(myRecyclerViewAdapter, arr, Currment, REFRESH_COMPLETE);
		// recycler中item监听事件
		myRecyclerViewAdapter.setOnItemClickListener(new OnItemClickLitener() {

			@Override
			public void onItemClick(View view, int position) {
				// Toast.makeText(context, position + " click",
				// Toast.LENGTH_SHORT).show();
				if (view != null) {
					PublishTravel pt = arr.get(position);
					// sendContent(pt,position);
					sendUserPicture(pt, position);
				}

			}
		});
		//
		// recyclerView的滚动监听，上拉加载更多
		mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					// mSwiperefreshlayout.setRefreshing(true);
					// 上拉加载更多数据源
					// && lastVisibleItem + 1 ==
					// myRecyclerViewAdapter.getItemCount()
					Log.i("flag", "" + flag);
					if (flag) {
						nextP = nowP++;
						mRenovate.initData(myRecyclerViewAdapter, arr, Currment, nextP);
						mHandler.sendEmptyMessageDelayed(MORE_ITEM, 2000);
					} else {
						Toast.makeText(MyApplication.getContext(), "没有更多数据", Toast.LENGTH_SHORT).show();
						nowP = REFRESH_COMPLETE;
						nextP = 0;
						mHandler.sendEmptyMessageDelayed(MORE_ITEM, 2000);
					}
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
			}

		});
		news_RecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					// mSwiperefreshlayout.setRefreshing(true);
					// 上拉加载更多数据源
					// && lastVisibleItem + 1 ==
					// myRecyclerViewAdapter.getItemCount()
					if(lastVisibleItem + 1 ==myRecyclerViewAdapter.getItemCount()){
					Log.i("flag", "" + flag);
					if (flag) {
						nextP = nowP++;
						mRenovate.initData(myRecyclerViewAdapter, arr, Currment, nextP);
						mHandler.sendEmptyMessageDelayed(NEW_MORE_ITEM, 2000);
					} else {
						Toast.makeText(MyApplication.getContext(), "没有更多数据", Toast.LENGTH_SHORT).show();
						nowP = REFRESH_COMPLETE;
						nextP = 0;
						mHandler.sendEmptyMessageDelayed(NEW_MORE_ITEM, 2000);
					}
					}
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
			}

		});
		aim_RecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					// mSwiperefreshlayout.setRefreshing(true);
					// 上拉加载更多数据源
					// && lastVisibleItem + 1 ==
					// myRecyclerViewAdapter.getItemCount()
					Log.i("flag", "" + flag);
					if (flag) {
						nextP = nowP++;
						mRenovate.initData(myRecyclerViewAdapter, arr, Currment, nextP);
						mHandler.sendEmptyMessageDelayed(AIM_MORE_ITEM, 2000);
					} else {
						Toast.makeText(MyApplication.getContext(), "没有更多数据", Toast.LENGTH_SHORT).show();
						nowP = REFRESH_COMPLETE;
						nextP = 0;
						mHandler.sendEmptyMessageDelayed(AIM_MORE_ITEM, 2000);
					}
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
			}

		});
		shut_RecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					// mSwiperefreshlayout.setRefreshing(true);
					// 上拉加载更多数据源
					// && lastVisibleItem + 1 ==
					// myRecyclerViewAdapter.getItemCount()
					Log.i("flag", "" + flag);
					if (flag) {
						nextP = nowP++;
						mRenovate.initData(myRecyclerViewAdapter, arr, Currment, nextP);
						mHandler.sendEmptyMessageDelayed(SHUT_MORE_ITEM, 2000);
					} else {
						Toast.makeText(MyApplication.getContext(), "没有更多数据", Toast.LENGTH_SHORT).show();
						nowP = REFRESH_COMPLETE;
						nextP = 0;
						mHandler.sendEmptyMessageDelayed(SHUT_MORE_ITEM, 2000);
					}
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
			}

		});
		//设置适配器
		mRecyclerView.setAdapter(myRecyclerViewAdapter);
		news_RecyclerView.setAdapter(myRecyclerViewAdapter);
		aim_RecyclerView.setAdapter(myRecyclerViewAdapter);
		shut_RecyclerView.setAdapter(myRecyclerViewAdapter);
		// 下拉刷新控件
		mSwiperefreshlayout = (SwipeRefreshLayout) ah_Hot.findViewById(R.id.hot_swiperefreshlayout);
		news_Swiperefreshlayout = (SwipeRefreshLayout) ah_Newsest.findViewById(R.id.new_swiperefreshlayout);
		aim_Swiperefreshlayout = (SwipeRefreshLayout) ah_Aimplace.findViewById(R.id.aim_swiperefreshlayout);
		shut_Swiperefreshlayout = (SwipeRefreshLayout) ah_Shuttime.findViewById(R.id.shut_swiperefreshlayout);
		// 下拉刷新事件
		mSwiperefreshlayout.setOnRefreshListener(new OnRefreshListener() {// 下拉刷新
			@Override
			public void onRefresh() { // TODO Auto-generated method stub //
				mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);

				Log.i("refresh", "热门下拉刷新+onrefresh");
			}
		});
		news_Swiperefreshlayout.setOnRefreshListener(new OnRefreshListener() {// 下拉刷新
			@Override
			public void onRefresh() { // TODO Auto-generated method stub //
				mHandler.sendEmptyMessageDelayed(NEW_REFRESH_COMPLETE, 1000);

				Log.i("refresh", "最新下拉刷新+onrefresh");
			}
		});
		aim_Swiperefreshlayout.setOnRefreshListener(new OnRefreshListener() {// 下拉刷新
			@Override
			public void onRefresh() { // TODO Auto-generated method stub //
				mHandler.sendEmptyMessageDelayed(AIM_REFRESH_COMPLETE, 1000);

				Log.i("refresh", "AIM下拉刷新+onrefresh");
			}
		});
		shut_Swiperefreshlayout.setOnRefreshListener(new OnRefreshListener() {// 下拉刷新
			@Override
			public void onRefresh() { // TODO Auto-generated method stub //
				mHandler.sendEmptyMessageDelayed(SHUT_REFRESH_COMPLETE, 1000);

				Log.i("refresh", "SHUT下拉刷新+onrefresh");
			}
		});
		// 刷新控件颜色定义
		mSwiperefreshlayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);
		news_Swiperefreshlayout.setColorSchemeResources(android.R.color.holo_green_dark,
				android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
		aim_Swiperefreshlayout.setColorSchemeResources(android.R.color.holo_green_dark,
				android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
		shut_Swiperefreshlayout.setColorSchemeResources(android.R.color.holo_green_dark,
				android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
		//
		mViewPager.setAdapter(mViewPagerAdapter);
		mIndicator.setTabItemTitles(mTitleList);
		mIndicator.setVisibleTabCount(4);
		mIndicator.setViewPager(mViewPager, 0);
		mIndicator.setOnPageChangeListener(new PageOnChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				// viewpager页面改变时请求新数据
				Currment = arg0;
				mRenovate.initData(myRecyclerViewAdapter, arr, Currment, REFRESH_COMPLETE);
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

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (i != 0) {
			mRenovate.initData(myRecyclerViewAdapter, arr, Currment, REFRESH_COMPLETE);
		}
		i++;
		// myRecyclerViewAdapter.notifyDataSetChanged();
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
		mTitleList.add("随机");
		mTitleList.add("同城");

		// viewpager适配器

		mViewPagerAdapter = new MyViewPagerAdapter(mViewList);
	}

	public static boolean isFlag() {
		return flag;
	}

	public static void setFlag(boolean flag) {
		Home_home.flag = flag;
	}

}
