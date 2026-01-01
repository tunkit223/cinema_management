import { useState, useEffect } from "react";
import { PageHeader } from "@/components/ui/PageHeader";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  InvoiceStatisticsCards,
  InvoiceFilters,
  InvoiceTable,
  InvoiceDetailDialog,
} from "@/components/invoices";
import {
  searchInvoices,
  getInvoiceStatistics,
  getInvoiceDetail,
} from "@/services/invoiceService";
import type {
  InvoiceResponse,
  InvoiceDetailResponse,
  InvoiceStatisticsResponse,
  InvoiceStatus,
} from "@/types/InvoiceType/invoice";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
import { useNotificationStore } from "@/stores";
import { ChevronLeft, ChevronRight } from "lucide-react";

export const InvoiceList = () => {
  const [invoices, setInvoices] = useState<InvoiceResponse[]>([]);
  const [statistics, setStatistics] = useState<InvoiceStatisticsResponse | null>(null);
  const [selectedInvoice, setSelectedInvoice] = useState<InvoiceDetailResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [statisticsLoading, setStatisticsLoading] = useState(false);
  const [detailDialogOpen, setDetailDialogOpen] = useState(false);

  // Pagination
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 10;

  // Filters
  const [filters, setFilters] = useState<{
    search: string;
    status: InvoiceStatus | "ALL";
  }>({
    search: "",
    status: "ALL",
  });

  const addNotification = useNotificationStore((state) => state.addNotification);

  // Fetch invoices
  const fetchInvoices = async (page: number = 0) => {
    try {
      setLoading(true);
      const params = {
        page,
        size: pageSize,
        search: filters.search || undefined,
        status: filters.status !== "ALL" ? filters.status : undefined,
      };

      const response = await searchInvoices(params);
      setInvoices(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
      setCurrentPage(page);
    } catch (error) {
      addNotification({
        type: "error",
        message: "Failed to load invoices",
      });
    } finally {
      setLoading(false);
    }
  };

  // Fetch statistics
  const fetchStatistics = async () => {
    try {
      setStatisticsLoading(true);
      const stats = await getInvoiceStatistics();
      setStatistics(stats);
    } catch (error) {
      addNotification({
        type: "error",
        message: "Failed to load statistics",
      });
    } finally {
      setStatisticsLoading(false);
    }
  };

  // Handle view invoice detail
  const handleViewDetail = async (invoice: InvoiceResponse) => {
    try {
      const detail = await getInvoiceDetail(invoice.id);
      setSelectedInvoice(detail);
      setDetailDialogOpen(true);
    } catch (error) {
      addNotification({
        type: "error",
        message: "Failed to load invoice details",
      });
    }
  };

  // Handle filter change
  const handleFilterChange = (newFilters: {
    search: string;
    status: InvoiceStatus | "ALL";
  }) => {
    setFilters(newFilters);
    setCurrentPage(0);
  };

  // Handle invoice update
  const handleInvoiceUpdate = () => {
    fetchInvoices(currentPage);
    fetchStatistics();
  };

  // Load data on mount and filter change
  useEffect(() => {
    fetchInvoices(0);
  }, [filters]);

  useEffect(() => {
    fetchStatistics();
  }, []);

  // Pagination handlers
  const handlePageChange = (page: number) => {
    if (page >= 0 && page < totalPages) {
      fetchInvoices(page);
    }
  };

  return (
    <div className="p-6 space-y-6">
      <PageHeader
        title="Invoice Management"
        description="View and manage payment invoices"
      />

      {/* Statistics Cards */}
      <InvoiceStatisticsCards
        statistics={statistics}
        loading={statisticsLoading}
      />

      {/* Filters */}
      <Card className="p-4">
        <InvoiceFilters onFilter={handleFilterChange} disabled={loading} />
      </Card>

      {/* Results Summary */}
      {!loading && invoices.length > 0 && (
        <div className="text-sm text-muted-foreground">
          Showing {currentPage * pageSize + 1} -{" "}
          {Math.min((currentPage + 1) * pageSize, totalElements)} of{" "}
          {totalElements} invoices
        </div>
      )}

      {/* Invoice Table */}
      <InvoiceTable
        invoices={invoices}
        loading={loading}
        onViewDetail={handleViewDetail}
      />

      {/* Pagination */}
      {!loading && totalPages > 1 && (
        <div className="flex items-center justify-between">
          <div className="text-sm text-muted-foreground">
            Page {currentPage + 1} of {totalPages}
          </div>
          <Pagination>
            <PaginationContent>
              <PaginationItem>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handlePageChange(currentPage - 1)}
                  disabled={currentPage === 0}
                >
                  <ChevronLeft className="h-4 w-4 mr-1" />
                  Previous
                </Button>
              </PaginationItem>

              {[...Array(Math.min(5, totalPages))].map((_, idx) => {
                let pageNum = currentPage;
                if (currentPage < 2) {
                  pageNum = idx;
                } else if (currentPage >= totalPages - 2) {
                  pageNum = totalPages - 5 + idx;
                } else {
                  pageNum = currentPage - 2 + idx;
                }

                if (pageNum < 0 || pageNum >= totalPages) return null;

                return (
                  <PaginationItem key={pageNum}>
                    <PaginationLink
                      onClick={() => handlePageChange(pageNum)}
                      isActive={currentPage === pageNum}
                    >
                      {pageNum + 1}
                    </PaginationLink>
                  </PaginationItem>
                );
              })}

              <PaginationItem>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handlePageChange(currentPage + 1)}
                  disabled={currentPage === totalPages - 1}
                >
                  Next
                  <ChevronRight className="h-4 w-4 ml-1" />
                </Button>
              </PaginationItem>
            </PaginationContent>
          </Pagination>
        </div>
      )}

      {/* Invoice Detail Dialog */}
      <InvoiceDetailDialog
        invoice={selectedInvoice}
        open={detailDialogOpen}
        onClose={() => {
          setDetailDialogOpen(false);
          setSelectedInvoice(null);
        }}
        onUpdate={handleInvoiceUpdate}
      />
    </div>
  );
};
