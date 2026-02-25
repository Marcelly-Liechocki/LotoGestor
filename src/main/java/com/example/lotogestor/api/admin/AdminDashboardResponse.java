package com.example.lotogestor.api.admin;

import java.math.BigDecimal;

public record AdminDashboardResponse(
  BigDecimal instantaneas,
  BigDecimal marketplace,
  BigDecimal telesenas,
  BigDecimal totalReceber
) {}
