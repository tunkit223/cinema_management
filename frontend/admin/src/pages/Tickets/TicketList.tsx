import { PageHeader } from "@/components/ui/PageHeader";
import { Card } from "@/components/ui/card";
import { Ticket } from "lucide-react";

export function TicketList() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Tickets Management"
        description="View and manage sold tickets"
      />

      <Card className="p-8">
        <div className="flex flex-col items-center justify-center space-y-4 text-center">
          <div className="rounded-full bg-orange-100 p-6">
            <Ticket className="h-12 w-12 text-orange-600" />
          </div>
          <h3 className="text-2xl font-semibold">Tickets Management</h3>
          <p className="text-muted-foreground max-w-md">
            This is the tickets management page. Here you can view all sold
            tickets and manage ticket transactions.
          </p>
        </div>
      </Card>
    </div>
  );
}
