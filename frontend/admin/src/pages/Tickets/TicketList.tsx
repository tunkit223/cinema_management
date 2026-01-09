import { useState } from "react";
import { PageHeader } from "@/components/ui/PageHeader";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { Ticket, Search, CheckCircle2, XCircle, Clock, Calendar, MapPin, DollarSign } from "lucide-react";
import { ticketService, TicketStatus } from "@/services/ticketService";
import type { TicketResponse } from "@/services/ticketService";
import { useNotificationStore } from "@/stores/useNotificationStore";
import { LoadingSpinner } from "@/components/ui/LoadingSpinner";

export function TicketList() {
  const [ticketCode, setTicketCode] = useState("");
  const [ticket, setTicket] = useState<TicketResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [checkingIn, setCheckingIn] = useState(false);
  const { addNotification } = useNotificationStore();

  const handleSearchTicket = async () => {
    if (!ticketCode.trim()) {
      addNotification({
        type: "error",
        title: "Error",
        message: "Please enter a ticket code",
      });
      return;
    }

    setLoading(true);
    try {
      const data = await ticketService.getTicketByCode(ticketCode);
      setTicket(data);
      addNotification({
        type: "success",
        title: "Success",
        message: "Ticket found successfully",
      });
    } catch (error: any) {
      addNotification({
        type: "error",
        title: "Error",
        message: error.response?.data?.message || "Ticket not found",
      });
      setTicket(null);
    } finally {
      setLoading(false);
    }
  };

  const handleCheckIn = async () => {
    if (!ticket) return;

    setCheckingIn(true);
    try {
      await ticketService.checkInTicket(ticket.ticketCode);
      addNotification({
        type: "success",
        title: "Success",
        message: "Ticket checked in successfully",
      });
      // Refresh ticket data
      const updatedTicket = await ticketService.getTicketByCode(ticket.ticketCode);
      setTicket(updatedTicket);
    } catch (error: any) {
      addNotification({
        type: "error",
        title: "Error",
        message: error.response?.data?.message || "Failed to check in ticket",
      });
    } finally {
      setCheckingIn(false);
    }
  };

  const getStatusBadge = (status: TicketStatus) => {
    switch (status) {
      case TicketStatus.ACTIVE:
        return <Badge className="bg-green-500 hover:bg-green-600">Active</Badge>;
      case TicketStatus.USED:
        return <Badge className="bg-blue-500 hover:bg-blue-600">Checked In</Badge>;
      case TicketStatus.EXPIRED:
        return <Badge variant="destructive">Expired</Badge>;
      case TicketStatus.CANCELLED:
        return <Badge variant="secondary">Cancelled</Badge>;
      default:
        return <Badge variant="outline">{status}</Badge>;
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString("en-US", {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };
  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
      maximumFractionDigits: 0,
    }).format(price);
  };

  const qrImageUrl = ticket?.qrContent
    ? `https://api.qrserver.com/v1/create-qr-code/?size=240x240&data=${encodeURIComponent(ticket.qrContent)}`
    : "";

  return (
    <div className="space-y-6">
      <PageHeader
        title="Ticket Management"
        description="Search and check-in customer tickets"
      />

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Ticket className="h-5 w-5" />
            Ticket Search
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex gap-4">
            <div className="flex-1">
              <Label htmlFor="ticketCode">Ticket Code</Label>
              <Input
                id="ticketCode"
                placeholder="Enter ticket code"
                value={ticketCode}
                onChange={(e) => setTicketCode(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter") {
                    handleSearchTicket();
                  }
                }}
              />
            </div>
            <div className="flex items-end">
              <Button onClick={handleSearchTicket} disabled={loading}>
                {loading ? (
                  <>
                    <LoadingSpinner size="sm" fullScreen={false} message="" />
                    <span className="ml-2">Searching...</span>
                  </>
                ) : (
                  <>
                    <Search className="mr-2 h-4 w-4" />
                    Search
                  </>
                )}
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      {ticket ? (
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle>Ticket Details</CardTitle>
              {getStatusBadge(ticket.status)}
            </div>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="grid gap-4 md:grid-cols-2">
              <div className="space-y-2">
                <div className="flex items-start gap-3">
                  <Ticket className="h-5 w-5 text-muted-foreground mt-0.5" />
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">Ticket Code</p>
                    <p className="text-lg font-semibold">{ticket.ticketCode}</p>
                  </div>
                </div>
              </div>

              <div className="space-y-2">
                <div className="flex items-start gap-3">
                  <Calendar className="h-5 w-5 text-muted-foreground mt-0.5" />
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">Movie</p>
                    <p className="text-lg font-semibold">{ticket.movieTitle}</p>
                  </div>
                </div>
              </div>

              <div className="space-y-2">
                <div className="flex items-start gap-3">
                  <Clock className="h-5 w-5 text-muted-foreground mt-0.5" />
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">Showtime</p>
                    <p className="text-base">{formatDate(ticket.startTime)}</p>
                  </div>
                </div>
              </div>

              <div className="space-y-2">
                <div className="flex items-start gap-3">
                  <MapPin className="h-5 w-5 text-muted-foreground mt-0.5" />
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">Seat</p>
                    <p className="text-base font-medium">{ticket.seatName}</p>
                  </div>
                </div>
              </div>

              <div className="space-y-2">
                <div className="flex items-start gap-3">
                  <DollarSign className="h-5 w-5 text-muted-foreground mt-0.5" />
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">Price</p>
                    <p className="text-base font-medium">{formatPrice(ticket.price)}</p>
                  </div>
                </div>
              </div>

              <div className="space-y-2">
                <div className="flex items-start gap-3">
                  <Clock className="h-5 w-5 text-muted-foreground mt-0.5" />
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">Expires At</p>
                    <p className="text-base">{formatDate(ticket.expiresAt)}</p>
                  </div>
                </div>
              </div>

              <div className="space-y-2">
                <div className="flex items-start gap-3">
                  <Calendar className="h-5 w-5 text-muted-foreground mt-0.5" />
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">Created At</p>
                    <p className="text-base">{formatDate(ticket.createdAt)}</p>
                  </div>
                </div>
              </div>
            </div>

            {ticket.qrContent && (
              <>
                <Separator />
                <div className="grid gap-4 md:grid-cols-[240px,1fr] items-start">
                  <div className="flex justify-center">
                    <img
                      src={qrImageUrl}
                      alt="Ticket QR"
                      className="h-60 w-60 rounded-lg border bg-white p-3 shadow-sm"
                    />
                  </div>
                  <div className="space-y-2">
                    <p className="text-sm font-medium text-muted-foreground">QR Code Content</p>
                    <div className="rounded-md bg-muted p-3">
                      <code className="text-sm break-all">{ticket.qrContent}</code>
                    </div>
                  </div>
                </div>
              </>
            )}

            <Separator />

            <div className="flex justify-end gap-4">
              {ticket.status === TicketStatus.ACTIVE ? (
                <Button
                  size="lg"
                  onClick={handleCheckIn}
                  disabled={checkingIn}
                  className="bg-green-600 hover:bg-green-700"
                >
                  {checkingIn ? (
                    <>
                      <LoadingSpinner size="sm" fullScreen={false} message="" />
                      <span className="ml-2">Checking In...</span>
                    </>
                  ) : (
                    <>
                      <CheckCircle2 className="mr-2 h-5 w-5" />
                      Check In Ticket
                    </>
                  )}
                </Button>
              ) : (
                <div className="flex items-center gap-2 text-muted-foreground">
                  {ticket.status === TicketStatus.CHECKED_IN ? (
                    <>
                      <CheckCircle2 className="h-5 w-5 text-blue-500" />
                      <span>This ticket has already been checked in</span>
                    </>
                  ) : (
                    <>
                      <XCircle className="h-5 w-5 text-red-500" />
                      <span>This ticket cannot be checked in ({ticket.status.toLowerCase()})</span>
                    </>
                  )}
                </div>
              )}
            </div>
          </CardContent>
        </Card>
      ) : (
        <Card className="p-8">
          <div className="flex flex-col items-center justify-center space-y-4 text-center">
            <div className="rounded-full bg-orange-100 p-6">
              <Search className="h-12 w-12 text-orange-600" />
            </div>
            <h3 className="text-2xl font-semibold">Please Enter Ticket Information</h3>
            <p className="text-muted-foreground max-w-md">
              Enter a ticket code in the search box above to view ticket details and perform check-in
            </p>
          </div>
        </Card>
      )}
    </div>
  );
}
