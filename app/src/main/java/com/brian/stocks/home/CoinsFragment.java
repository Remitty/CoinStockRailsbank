package com.brian.stocks.home;

import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brian.stocks.R;
import com.brian.stocks.SendData;
import com.brian.stocks.home.adapters.CoinAdapter;
import com.brian.stocks.helper.SharedHelper;
import com.brian.stocks.helper.URLHelper;
import com.brian.stocks.model.CoinInfo;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.IO.Options;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.impl.client.action.ActionDispatcher;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;
import com.xuhao.didi.socket.client.sdk.client.connection.NoneReconnect;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class CoinsFragment extends Fragment {

    private LoadToast loadToast;
    private View rootView;
    private CoinAdapter mAdapter;
    private List<CoinInfo> coinList = new ArrayList<>();
    private RecyclerView coinListView;
    private TextView mTotalBalance, mTotalEffect, mUSDBalance;
    private Handler handler;
    private SwipeRefreshLayout refreshLayout;
    private String CoinSymbol, CoinId;

    private OkSocketOptions mOkOptions;
    private IConnectionManager mManager;
    private ConnectionInfo mInfo;

    private SocketActionAdapter adapter = new SocketActionAdapter() {

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            JSONObject object = new JSONObject();
            JSONObject subscription = new JSONObject();
            try {
                object.put("event", "ping");
//                object.put("event", "subscribe");
//                String pair = "XBT/USD";
//                object.put("pair", pair);
//                subscription.put("name", "ticker");
//                object.put("subscription", subscription);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            mManager.send(new HandShakeBean());
//            mManager.send(new SendData(object.toString()));
//            String data = "{\"event\":\"subscribe\",\"pair\":[\"XBT/USD\"],\"subscription\":{\"name\":\"ticker\"}}";
            mManager.send(new SendData(object.toString()));
            Log.d("socket success", "Socket connect success");
        }

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            if (e != null) {
                Log.d("socket disconnet", e.getMessage());
            } else {
                Log.d("socketdisconnect", "(Disconnect Manually)");
            }
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            Log.d("socket faild", "(Connecting Failed)");
        }

        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            Log.d("socket response", str);
        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
            Log.d("socket write", str);
        }

        @Override
        public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
            Log.d("socket plse", str);
        }
    };

    private Socket mSocket;
    {
        try {
            Options option = new IO.Options();
            option.host="ws.kraken.com";
            option.port=443;
            option.forceNew = true;
//            option.path = "/wss";
            mSocket = IO.socket("https://ws.kraken.com", option);
        } catch (URISyntaxException e) {}
    }

    public CoinsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CoinsFragment newInstance() {
        CoinsFragment fragment = new CoinsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadToast = new LoadToast(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_coins, container, false);

        coinListView = (RecyclerView) rootView.findViewById(R.id.list_coins_view);
        mTotalBalance = rootView.findViewById(R.id.total_balance);
        mTotalEffect = rootView.findViewById(R.id.total_effect);
        mUSDBalance = rootView.findViewById(R.id.usd_balance);

        refreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);

        mAdapter = new CoinAdapter(coinList, getActivity(), true);
        coinListView.setLayoutManager(new LinearLayoutManager(getContext()));
        coinListView.setAdapter(mAdapter);

        mAdapter.setListener(new CoinAdapter.Listener() {
            @Override
            public void OnDeposit(int position) {
//                index = position;
//                GoToTransaction(position);
                CoinSymbol = coinList.get(position).getCoinSymbol();
                CoinId = coinList.get(position).getCoinId();
                doGenerateWalletAddress();
            }
        });

//        mSocket.on(Socket.EVENT_CONNECT,onConnect);
//        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
//        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
////        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
//        mSocket.connect();
//        mSocket.on("updatecoins", updateValue);
//
//        JSONObject object = new JSONObject();
//        try {
//            object.put("pair", "XBT/USD");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        mSocket.emit("subscribe", object);

//        getKrakenAPIToken();
        getAllCoins(false);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getAllCoins(true);
                handler.postDelayed(this, 60000);
            }
        }, 60000);

        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refresh();
                    }
                }
        );

//        try {
//            SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
//            SSLSocket socket = (SSLSocket)factory.createSocket("ws.kraken.com", 443);
//            socket.connect();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        final Handler handler = new Handler();
        mInfo = new ConnectionInfo("ws.kraken.com", 443);
        mOkOptions = new OkSocketOptions.Builder()
                .setReaderProtocol(new IReaderProtocol() {
                    @Override
                    public int getHeaderLength() {
                        return 10;
                    }

                    @Override
                    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
                        ByteBuffer bb = ByteBuffer.allocate(header.length);
                        bb.order(byteOrder);
                        bb.put(header);
                        Log.d("socket header data", Arrays.toString(bb.array()));
                        int value = header.length;
                        Log.d("socketheaderdata1", value+"");
                        return value;
                    }
                })
                .setReconnectionManager(new NoneReconnect())
                .setConnectTimeoutSecond(10)
                .setCallbackThreadModeToken(new OkSocketOptions.ThreadModeToken() {
                    @Override
                    public void handleCallbackEvent(ActionDispatcher.ActionRunnable runnable) {
                        handler.post(runnable);
                    }
                })
                .build();

        mManager = OkSocket.open(mInfo).option(mOkOptions);
        mManager.registerReceiver(adapter);

        mManager.connect();

        return rootView;
    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mSocket.connected())
                        Log.d("socketsuccess", mSocket.id());
                }
            });
        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("socket disconnect", "diconnected");

                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("socket connect eorrr", "Error connecting");

                }
            });
        }
    };


    private void getKrakenAPIToken() {
        if(getContext() != null){
            AndroidNetworking.get("https://api.kraken.com/0/private/GetWebSocketsToken")
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("kraknetoekn", response.toString());
//                            Log.d("kraknetoken", response.optJSONObject("subscription").optString("token"));
                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });
        }
    }

    private void showWalletAddressDialog(String generatedAddress) {
        DepositDialog mContentDialog = new DepositDialog(R.layout.fragment_coin_deposit, generatedAddress, CoinSymbol);
        mContentDialog.setListener(new DepositDialog.Listener() {

            @Override
            public void onOk() {
//                mContentDialog.dismiss();
                Toast.makeText(getContext(), "Copied successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
//                mContentDialog.dismiss();
            }
        });

        mContentDialog.show(this.getActivity().getSupportFragmentManager(), "deposit");
    }

    private void refresh() {
        refreshLayout.setRefreshing(true);
        JSONObject jsonObject = new JSONObject();

        Log.d("access_token", SharedHelper.getKey(getContext(), "access_token"));
        if(getContext() != null)
            AndroidNetworking.get(URLHelper.GET_ALL_COINS)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", "" + response);
                            refreshLayout.setRefreshing(false);
                            coinList.clear();

                            try {
                                mTotalBalance.setText("$ " + response.getString("total_balance"));
                                JSONArray coins = response.optJSONArray("coins");
                                if(coins != null)
                                    for(int i = 0; i < coins.length(); i ++) {
                                        try {
                                            Log.d("coinitem", coins.get(i).toString());
                                            coinList.add(new CoinInfo((JSONObject) coins.get(i)));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                mAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError error) {
                            refreshLayout.setRefreshing(false);
                            // handle error
                            Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getMessage());
                        }
                    });
    }

    private void getAllCoins(boolean update) {
        if(!update)
            loadToast.show();
        JSONObject jsonObject = new JSONObject();

        Log.d("access_token", SharedHelper.getKey(getContext(), "access_token"));
        if(getContext() != null)
        AndroidNetworking.get(URLHelper.GET_ALL_COINS)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
                        loadToast.success();
                        coinList.clear();

                        try {
                            mTotalBalance.setText("$ " + response.getString("total_balance"));
                            mTotalEffect.setText(response.getString("total_effect")+" %");
                            mUSDBalance.setText(response.getString("usd_balance"));
                            if(response.getString("total_effect").startsWith("-"))
                                mTotalEffect.setTextColor(RED);
                            else mTotalEffect.setTextColor(GREEN);

                            JSONArray coins = response.getJSONArray("coins");
                            for(int i = 0; i < coins.length(); i ++) {
                                try {
                                    coinList.add(new CoinInfo((JSONObject) coins.get(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        // handle error
                        Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getMessage());
                    }
                });
    }

    private void doGenerateWalletAddress() {
        loadToast.show();
//        loadToast.show();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("coin", CoinId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("deposit param", jsonObject.toString());
        if(getContext() != null)
            AndroidNetworking.post(URLHelper.COIN_DEPOSIT)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", "" + response);
                            loadToast.success();
                            if(response.optBoolean("success")) {
                                try {
                                    showWalletAddressDialog(response.getString("address"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                                Toast.makeText(getContext(), response.optString("error"), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getMessage());
                            Log.d("errorm", "" + error.getErrorBody());

                        }
                    });
    }

    private Emitter.Listener updateValue = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.d("socket connection", data.toString());
                }
            });
        }
    };

    @Override
    public void onResume() {
        super.onResume();

//        getAllCoins();
//        handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getAllCoinsUpdae();
//                handler.postDelayed(this, 5000);
//            }
//        }, 10000);
    }

    @Override
    public void onPause() {

        super.onPause();
        mSocket.off("updatecoins", updateValue);
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
