package com.example.fluxtest;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

public class MyFilter implements Filter{

	private EventNotify eventNotify;
	
	public MyFilter(EventNotify eventNotify) {
		this.eventNotify = eventNotify;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("필터 실행됨");
		
		
		HttpServletResponse servletResponse = (HttpServletResponse) response;
		servletResponse.setContentType("text/event-stream; charset=utf-8");
		
		PrintWriter out = servletResponse.getWriter();
		
		// 1.Reactive Streams 라이브러리를 쓰면 표준을 지켜서 응답을 할 수 있다.
		for(int i = 0; i < 5 ; i++) {
			out.print("응답: "+i+"\n");
			out.flush(); //버퍼를 비우다.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 2. SSE Emitter 라이브러리를 사용하면 편하게 쓸 수 있다.
		while(true) {
			try {
				if(eventNotify.getChange()) {
					int lastIndex = eventNotify.getEvents().size()-1;
					out.print("응답: "+eventNotify.getEvents().get(lastIndex)+"\n");
					out.flush(); //버퍼를 비우다.
					eventNotify.setChange(false);
				}
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// 3. WebFlux -> Reactive Streams 이 친구가 적용된 stream을 배우고 (비동기 단일스레드동작)
		// 4. ServletMVC -> Reactive Streams 이 친구가 적용된 stream을 배우고 (멀티스레드동작)
			
	}

}
