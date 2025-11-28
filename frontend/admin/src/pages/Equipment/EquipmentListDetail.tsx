import { useState } from "react";
import { Button } from "@/components/ui/button";
import { ArrowLeft, Plus, Trash2, Edit } from "lucide-react";
import {
  deleteEquipment,
  type Equipment,
  type EquipmentCategory,
} from "@/services/equipmentService";
import EquipmentModal from "./EquipmentModal";

interface Room {
  id: string;
  name: string;
}

interface EquipmentListDetailProps {
  room: Room;
  equipment: Equipment[];
  categories: EquipmentCategory[];
  loading: boolean;
  error: string | null;
  onBack: () => void;
  onEquipmentUpdated: () => Promise<void>;
}

export default function EquipmentListDetail({
  room,
  equipment,
  categories,
  loading,
  error,
  onBack,
  onEquipmentUpdated,
}: EquipmentListDetailProps) {
  const [showModal, setShowModal] = useState(false);
  const [editingEquipment, setEditingEquipment] = useState<Equipment | null>(null);

  const handleCreate = () => {
    setEditingEquipment(null);
    setShowModal(true);
  };

  const handleEdit = (eq: Equipment) => {
    setEditingEquipment(eq);
    setShowModal(true);
  };

  const handleDelete = async (equipmentId: string) => {
    if (!window.confirm("Are you sure you want to delete this equipment?")) {
      return;
    }

    try {
      await deleteEquipment(equipmentId);
      await onEquipmentUpdated();
    } catch (err: any) {
      console.error("Error deleting equipment:", err);
    }
  };

  const getCategoryName = (categoryId: string) => {
    return categories.find((c) => c.id === categoryId)?.name || "Unknown";
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case "ACTIVE":
        return "bg-green-100 text-green-800";
      case "MAINTENANCE":
        return "bg-yellow-100 text-yellow-800";
      case "BROKEN":
        return "bg-red-100 text-red-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <Button
          onClick={onBack}
          variant="ghost"
          className="text-blue-600 hover:text-blue-800"
        >
          <ArrowLeft className="h-4 w-4 mr-2" />
          Back to Rooms
        </Button>
      </div>

      <div className="bg-white rounded-lg shadow-sm p-6">
        <div className="flex justify-between items-center mb-6">
          <div>
            <h2 className="text-3xl font-bold">{room.name}</h2>
            <p className="text-gray-500 mt-1">Equipment Management</p>
          </div>
          <Button
            onClick={handleCreate}
            className="bg-blue-600 hover:bg-blue-700 text-white"
          >
            <Plus className="h-4 w-4 mr-2" />
            Add Equipment
          </Button>
        </div>

        {error && (
          <div className="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
            {error}
          </div>
        )}

        {loading ? (
          <div className="text-center py-8">Loading...</div>
        ) : equipment.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            No equipment found. Add one to get started.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full border-collapse">
              <thead>
                <tr className="border-b">
                  <th className="text-left py-3 px-4 font-semibold">Name</th>
                  <th className="text-left py-3 px-4 font-semibold">Category</th>
                  <th className="text-left py-3 px-4 font-semibold">Serial Number</th>
                  <th className="text-left py-3 px-4 font-semibold">Status</th>
                  <th className="text-left py-3 px-4 font-semibold">Purchase Date</th>
                  <th className="text-left py-3 px-4 font-semibold">Actions</th>
                </tr>
              </thead>
              <tbody>
                {equipment.map((eq) => (
                  <tr key={eq.id} className="border-b hover:bg-gray-50">
                    <td className="py-3 px-4 font-medium">{eq.name}</td>
                    <td className="py-3 px-4">
                      <span className="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm">
                        {getCategoryName(eq.categoryId)}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-sm">{eq.serialNumber || "-"}</td>
                    <td className="py-3 px-4">
                      <span className={`px-3 py-1 rounded-full text-sm ${getStatusColor(eq.status)}`}>
                        {eq.status}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-sm">
                      {eq.purchaseDate
                        ? new Date(eq.purchaseDate).toLocaleDateString()
                        : "-"}
                    </td>
                    <td className="py-3 px-4">
                      <div className="flex gap-2">
                        <Button
                          onClick={() => handleEdit(eq)}
                          variant="ghost"
                          size="sm"
                          className="text-blue-600 hover:text-blue-800"
                        >
                          <Edit className="h-4 w-4" />
                        </Button>
                        <Button
                          onClick={() => handleDelete(eq.id)}
                          variant="ghost"
                          size="sm"
                          className="text-red-600 hover:text-red-800"
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {showModal && (
        <EquipmentModal
          room={room}
          equipment={editingEquipment}
          categories={categories}
          onClose={() => setShowModal(false)}
          onSave={onEquipmentUpdated}
        />
      )}
    </div>
  );
}
