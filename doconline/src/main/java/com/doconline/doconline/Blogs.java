package com.doconline.doconline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.doconline.doconline.helper.BaseActivity;

public class Blogs extends BaseActivity {
   // ProgressBar progress;
    RelativeLayout layout_loading;
    WebView webView;

    String blogURL = "https://www.doconline.com/blog";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogs);
        layout_loading = findViewById(R.id.layout_loading);
        layout_loading.setVisibility(View.VISIBLE);


        // progress=findViewById(R.id.progress);
        if (getIntent().getExtras() != null) {
            blogURL = getIntent().getExtras().get("blog_url").toString();
        }

        layout_loading.setVisibility(View.GONE);

        Log.e("AAA","Blog URl is : "+blogURL);
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(blogURL);
        webView.setWebViewClient(new WebViewClient() {
                                     public void onPageFinished(final WebView webView, String url) {
                                         String document = "document.getElementsByClassName('mobile-menu-button')[0].style.visibility = 'hidden'";
                                         webView.evaluateJavascript(document, new ValueCallback<String>() {
                                             @Override
                                             public void onReceiveValue(String value) {
                                                 //Toast.makeText(Blogs.this, "working", Toast.LENGTH_SHORT).show();
                                             }
                                         });

                                         String document1 = "document.getElementsByClassName('header-sticky')[0].style.visibility = 'hidden'";
                                         webView.evaluateJavascript(document1, new ValueCallback<String>() {
                                             @Override
                                             public void onReceiveValue(String value) {
                                                 //Toast.makeText(Blogs.this, "working in scrol", Toast.LENGTH_SHORT).show();
                                             }
                                         });


                                         final String replacestring = "<!-- Chat Plugin-->\n" +
                                                 "        <script>(function(){var w=window;var ic=w.Intercom;if(typeof ic===\"function\"){ic('reattach_activator');ic('update',intercomSettings);}else{var d=document;var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};w.Intercom=i;function l(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://widget.intercom.io/widget/nuw5k6fq';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);}if(w.attachEvent){w.attachEvent('onload',l);}else{w.addEventListener('load',l,false);}}})()</script>\n" +
                                                 "        <!-- Chat Plugin-->";
                                         final String document2 = "document.documentElement.outerHTML.toString()";
                                         webView.evaluateJavascript(document2, new ValueCallback<String>() {
                                             @Override
                                             public void onReceiveValue(String value) {
                                                 Spanned htmlText = Html.fromHtml(value);
                                                 Log.e("AAA","chat window  : "+htmlText);
                                                 //String replaceddata = value.replace(replacestring,"");
                                                 //webView.loadData(replaceddata,".htm", TextUtils.htmlEncode(replaceddata));
                                             }
                                         });

                                        /* String document2 = "document.documentElement.outerHTML.toString()";
                                         webView.evaluateJavascript(document2, new ValueCallback<String>() {
                                             @Override
                                             public void onReceiveValue(String value) {
                                                 Log.e("AAA",value);
                                                 //Toast.makeText(Blogs.this, "working in scrol", Toast.LENGTH_SHORT).show();
                                             }
                                         });*/
                                     }
                                 });

        /*wvbrowser.evaluateJavascript(
                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String html) {
                        Log.d("HTML", html);
                        // code here
                    }
                });*/
                        /*if array.count >= 2 {

                    self.htmlString = str.replacingOccurrences(of:
                    "<!-- Chat Plugin-->\(array[1])<!-- Chat Plugin-->", with:"")

                    self.webView.loadHTMLString(self.htmlString !, baseURL:nil)

                }*/

        /*webView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                String document = "document.getElementsByClassName('header-sticky')[0].style.visibility = 'hidden'";
                webView.evaluateJavascript(document, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Toast.makeText(Blogs.this, "working in scrol", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });*/

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(Blogs.this, HomeActivity.class));
        finishAffinity();
    }
   /* public void showSearchProgressBar()
    {
        progress.setVisibility(View.VISIBLE);
    }

    public void hideSearchProgressBar()
    {
        progress.setVisibility(View.GONE);
    }*/
}
