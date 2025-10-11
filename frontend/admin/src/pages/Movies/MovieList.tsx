import { PageHeader } from "@/components/ui/PageHeader";
import { Card } from "@/components/ui/card";
import { Film } from "lucide-react";

export function MovieList() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Movies Management"
        description="Manage all movies in the system"
      />

      <Card className="p-8">
        <div className="flex flex-col items-center justify-center space-y-4 text-center">
          <div className="rounded-full bg-blue-100 p-6">
            <Film className="h-12 w-12 text-blue-600" />
          </div>
          <h3 className="text-2xl font-semibold">Movies Management</h3>
          <p className="text-muted-foreground max-w-md">
            This is the movies management page. Here you can view, create, edit,
            and delete movies.
          </p>
        </div>
      </Card>
    </div>
  );
}
