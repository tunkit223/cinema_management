import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import type {
  NotificationRequest,
  NotificationPriority,
} from "@/types/NotificationType/Notification";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { X, FileText, ArrowLeft } from "lucide-react";
import {
  getAllTemplates,
  type NotificationTemplate,
} from "@/services/notificationTemplateService";
import { sendNotification } from "@/services/notificationService";
import { useNotificationStore } from "@/stores";
import { ROUTES } from "@/constants/routes";
import { PageHeader } from "@/components/ui/PageHeader";
import { Card } from "@/components/ui/card";
import {
  categoryOptions,
  priorityOptions,
} from "@/constants/notificationConfig";

const RECIPIENT_TYPE_OPTIONS = [
  { value: "CUSTOMER", label: "Customer" },
  { value: "STAFF", label: "Staff" },
  { value: "ADMIN", label: "Admin" },
];

const CHANNEL_OPTIONS = [
  { value: "EMAIL", label: "Email" },
  { value: "IN_APP", label: "In-App" },
  { value: "SMS", label: "SMS" },
];

export const SendNotification = () => {
  const navigate = useNavigate();
  const [templates, setTemplates] = useState<NotificationTemplate[]>([]);
  const [loadingTemplates, setLoadingTemplates] = useState(true);
  const [selectedTemplate, setSelectedTemplate] =
    useState<NotificationTemplate | null>(null);
  const [sending, setSending] = useState(false);
  const addNotification = useNotificationStore(
    (state) => state.addNotification
  );

  const [formData, setFormData] = useState<NotificationRequest>({
    templateCode: "",
    recipientId: "",
    recipientType: "CUSTOMER",
    category: "BOOKING",
    channels: ["EMAIL", "IN_APP"],
    metadata: {},
    priority: "HIGH",
  });

  const [metadataInput, setMetadataInput] = useState("");
  const [errors, setErrors] = useState<Record<string, string>>({});

  // Load templates from database
  useEffect(() => {
    const loadTemplates = async () => {
      try {
        setLoadingTemplates(true);
        const data = await getAllTemplates();
        setTemplates(data);

        // Set first template as default if available
        if (data.length > 0) {
          setFormData((prev) => ({
            ...prev,
            templateCode: data[0].templateCode,
            priority: data[0].priority as NotificationPriority,
          }));
          setSelectedTemplate(data[0]);
        }
      } catch (error: any) {
        addNotification({
          type: "error",
          title: "Error",
          message: error?.response?.data?.message || "Failed to load templates",
        });
      } finally {
        setLoadingTemplates(false);
      }
    };

    loadTemplates();
  }, [addNotification]);

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >
  ) => {
    const { name, value } = e.target;

    // If template is changed, update selected template and priority
    if (name === "templateCode") {
      const template = templates.find((t) => t.templateCode === value);
      setSelectedTemplate(template || null);
      setFormData((prev) => ({
        ...prev,
        [name]: value,
        priority: (template?.priority as NotificationPriority) || prev.priority,
      }));
    } else {
      setFormData((prev) => ({
        ...prev,
        [name]: value,
      }));
    }

    // Clear error when user starts typing
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: "" }));
    }
  };

  const toggleChannel = (channel: string) => {
    setFormData((prev) => ({
      ...prev,
      channels: prev.channels.includes(channel)
        ? prev.channels.filter((c) => c !== channel)
        : [...prev.channels, channel],
    }));
  };

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.templateCode.trim()) {
      newErrors.templateCode = "Template is required";
    }

    if (!formData.recipientId.trim()) {
      newErrors.recipientId = "Recipient ID is required";
    }

    if (formData.channels.length === 0) {
      newErrors.channels = "At least one channel must be selected";
    }

    // Validate metadata JSON if provided
    if (metadataInput.trim()) {
      try {
        JSON.parse(metadataInput);
      } catch {
        newErrors.metadata = "Invalid JSON format";
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    // Parse metadata
    let metadata = {};
    if (metadataInput.trim()) {
      try {
        metadata = JSON.parse(metadataInput);
      } catch {
        setErrors((prev) => ({ ...prev, metadata: "Invalid JSON format" }));
        return;
      }
    }

    try {
      setSending(true);
      await sendNotification({
        ...formData,
        metadata,
      });

      addNotification({
        type: "success",
        title: "Success",
        message: "Notification sent successfully",
      });

      navigate(ROUTES.NOTIFICATIONS_LIST);
    } catch (error: any) {
      addNotification({
        type: "error",
        title: "Error",
        message:
          error?.response?.data?.message || "Failed to send notification",
      });
    } finally {
      setSending(false);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Send Notification"
        description="Create and send a new notification"
      />

      <Card className="p-6">
        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="grid grid-cols-2 gap-4">
            {/* Template Code */}
            <div className="space-y-2">
              <Label htmlFor="templateCode">
                Template <span className="text-destructive">*</span>
              </Label>
              <select
                id="templateCode"
                name="templateCode"
                value={formData.templateCode}
                onChange={handleChange}
                disabled={loadingTemplates || templates.length === 0}
                className={`flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50 ${
                  errors.templateCode ? "border-destructive" : ""
                }`}
              >
                {loadingTemplates ? (
                  <option>Loading templates...</option>
                ) : templates.length === 0 ? (
                  <option>No templates available</option>
                ) : (
                  templates.map((template) => (
                    <option key={template.id} value={template.templateCode}>
                      {template.templateCode}
                    </option>
                  ))
                )}
              </select>
              {errors.templateCode && (
                <p className="text-sm text-destructive">
                  {errors.templateCode}
                </p>
              )}
            </div>

            {/* Category */}
            <div className="space-y-2">
              <Label htmlFor="category">Category</Label>
              <select
                id="category"
                name="category"
                value={formData.category}
                onChange={handleChange}
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
              >
                {categoryOptions.map((cat) => (
                  <option key={cat.value} value={cat.value}>
                    {cat.label}
                  </option>
                ))}
              </select>
            </div>

            {/* Recipient ID */}
            <div className="space-y-2">
              <Label htmlFor="recipientId">
                Recipient ID <span className="text-destructive">*</span>
              </Label>
              <Input
                id="recipientId"
                name="recipientId"
                value={formData.recipientId}
                onChange={handleChange}
                placeholder="Enter recipient ID"
                className={errors.recipientId ? "border-destructive" : ""}
              />
              {errors.recipientId && (
                <p className="text-sm text-destructive">{errors.recipientId}</p>
              )}
            </div>

            {/* Recipient Type */}
            <div className="space-y-2">
              <Label htmlFor="recipientType">Recipient Type</Label>
              <select
                id="recipientType"
                name="recipientType"
                value={formData.recipientType}
                onChange={handleChange}
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
              >
                {RECIPIENT_TYPE_OPTIONS.map((type) => (
                  <option key={type.value} value={type.value}>
                    {type.label}
                  </option>
                ))}
              </select>
            </div>

            {/* Priority */}
            <div className="space-y-2 col-span-2">
              <Label htmlFor="priority">Priority</Label>
              <select
                id="priority"
                name="priority"
                value={formData.priority}
                onChange={handleChange}
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
              >
                {priorityOptions.map((priority) => (
                  <option key={priority.value} value={priority.value}>
                    {priority.label}
                  </option>
                ))}
              </select>
            </div>

            {/* Channels */}
            <div className="space-y-2 col-span-2">
              <Label>
                Channels <span className="text-destructive">*</span>
              </Label>
              <div className="flex flex-wrap gap-2">
                {CHANNEL_OPTIONS.map((channel) => (
                  <Badge
                    key={channel.value}
                    variant={
                      formData.channels.includes(channel.value)
                        ? "default"
                        : "outline"
                    }
                    className="cursor-pointer"
                    onClick={() => toggleChannel(channel.value)}
                  >
                    {channel.label}
                    {formData.channels.includes(channel.value) && (
                      <X className="w-3 h-3 ml-1" />
                    )}
                  </Badge>
                ))}
              </div>
              {errors.channels && (
                <p className="text-sm text-destructive">{errors.channels}</p>
              )}
            </div>

            {/* Metadata (JSON) */}
            <div className="space-y-2 col-span-2">
              <Label htmlFor="metadata">
                Metadata (JSON)
                <span className="text-xs text-muted-foreground ml-2">
                  Optional - Template variables
                </span>
              </Label>
              <Textarea
                id="metadata"
                value={metadataInput}
                onChange={(e) => setMetadataInput(e.target.value)}
                placeholder='{"customerName": "John Doe", "movieName": "Avatar"}'
                rows={4}
                className={errors.metadata ? "border-destructive" : ""}
              />
              {errors.metadata && (
                <p className="text-sm text-destructive">{errors.metadata}</p>
              )}
            </div>

            {/* Template Preview */}
            {selectedTemplate && (
              <div className="space-y-3 col-span-2 p-4 border border-border rounded-lg bg-muted/30">
                <div className="flex items-center gap-2 text-sm font-medium text-foreground">
                  <FileText className="w-4 h-4" />
                  Template Preview
                </div>
                <div className="space-y-2">
                  <div>
                    <p className="text-xs text-muted-foreground mb-1">
                      Title Template:
                    </p>
                    <p className="text-sm bg-background p-2 rounded border border-border">
                      {selectedTemplate.titleTemplate}
                    </p>
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground mb-1">
                      Content Template:
                    </p>
                    <div
                      className="text-sm bg-background p-2 rounded border border-border min-h-[100px]"
                      dangerouslySetInnerHTML={{
                        __html: selectedTemplate.contentTemplate,
                      }}
                    />
                  </div>
                  <div className="flex gap-2 items-center">
                    <p className="text-xs text-muted-foreground">Priority:</p>
                    <Badge variant="secondary">
                      {selectedTemplate.priority}
                    </Badge>
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Actions */}
          <div className="flex justify-end gap-2 pt-4">
            <Button
              type="button"
              variant="outline"
              onClick={() => navigate(ROUTES.NOTIFICATIONS_LIST)}
              className="cursor-pointer"
            >
              <ArrowLeft className="w-4 h-4 mr-2" />
              Cancel
            </Button>
            <Button type="submit" disabled={sending} className="cursor-pointer">
              {sending ? "Sending..." : "Send Notification"}
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
};
