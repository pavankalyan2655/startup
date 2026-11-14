package com.tgpj.dashboard.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tgpj.dashboard.dto.DashboardStatsResponse;
import com.tgpj.dashboard.service.DashboardService;

@RestController
@RequestMapping("/api/admin")
public class DashboardController {

	private final DashboardService dashboardService;

	public DashboardController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	@GetMapping("/dashboard/stats")
	@PreAuthorize("hasRole('ADMIN')")
	public DashboardStatsResponse getStats() {
		return dashboardService.getDashboardStats();
	}
}
