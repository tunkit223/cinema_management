"use client";

import { useEffect, useState } from "react";
import {
  getMyInfo,
  updateMyInfo,
  CustomerInfo,
} from "@/services/customerService";
import { getToken } from "@/services/localStorageService";
import { useRouter } from "next/navigation";
import { X } from "lucide-react";
import { getErrorMessage } from "@/lib/errors";
import { useToast } from "@/hooks/use-toast";

export default function ProfilePage() {
  const [userInfo, setUserInfo] = useState<CustomerInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [editError, setEditError] = useState<string | null>(null);
  const router = useRouter();
  const { toast } = useToast();

  useEffect(() => {
    const fetchUserInfo = async () => {
      const token = getToken();
      console.log("Token:", token);

      if (!token) {
        router.push("/");
        return;
      }

      try {
        const info = await getMyInfo();
        setUserInfo(info);
      } catch (err: any) {
        console.error("Failed to fetch user info:", err);
        setError("Failed to load profile information");
      } finally {
        setLoading(false);
      }
    };

    fetchUserInfo();
  }, [router]);

  const handleEditProfile = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setEditError(null);
    setIsSaving(true);

    const formData = new FormData(e.currentTarget);

    try {
      const updateData = {
        firstName: formData.get("firstName") as string,
        lastName: formData.get("lastName") as string,
        phoneNumber: formData.get("phoneNumber") as string,
        address: formData.get("address") as string,
        gender: formData.get("gender") as string,
        dob: formData.get("dob") as string,
      };

      console.log("Updating profile with:", updateData);

      const updatedInfo = await updateMyInfo(userInfo!.customerId, updateData);
      setUserInfo(updatedInfo);
      setIsEditModalOpen(false);

      // Show success toast
      toast({
        title: "Success",
        description: "Profile updated successfully!",
        variant: "default",
      });
    } catch (err: any) {
      console.error("Failed to update profile:", err);
      const errorMessage = getErrorMessage(err);
      setEditError(errorMessage);

      // Show error toast
      toast({
        title: "Error",
        description: errorMessage,
        variant: "destructive",
      });
    } finally {
      setIsSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-600 mb-4">{error}</p>
          <button
            onClick={() => router.push("/")}
            className="px-6 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700"
          >
            Back to Home
          </button>
        </div>
      </div>
    );
  }

  const fullName = userInfo
    ? `${userInfo.firstName} ${userInfo.lastName}`
    : "User";
  const initials = userInfo
    ? `${userInfo.firstName?.charAt(0)}${userInfo.lastName?.charAt(0)}`
    : "U";

  return (
    <div className="min-h-screen pt-20 pb-12 px-4">
      <div className="container mx-auto max-w-4xl">
        <h1 className="text-3xl font-bold mb-8">My Profile</h1>

        <div className="bg-card border border-border rounded-2xl p-8">
          <div className="flex items-center gap-6 mb-8">
            <div className="w-24 h-24 rounded-full bg-gradient-to-r from-purple-500 to-pink-500 flex items-center justify-center text-white text-3xl font-bold">
              {initials.toUpperCase()}
            </div>
            <div>
              <h2 className="text-2xl font-bold mb-1">{fullName}</h2>
              <p className="text-muted-foreground">@{userInfo?.username}</p>
              {userInfo?.loyaltyPoints !== undefined && (
                <p className="text-purple-600 font-semibold mt-1">
                  ⭐ {userInfo.loyaltyPoints} Loyalty Points
                </p>
              )}
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="text-sm text-muted-foreground font-medium">
                Username
              </label>
              <p className="text-lg mt-1">{userInfo?.username || "N/A"}</p>
            </div>

            <div>
              <label className="text-sm text-muted-foreground font-medium">
                Email
              </label>
              <p className="text-lg mt-1">{userInfo?.email || "N/A"}</p>
            </div>

            <div>
              <label className="text-sm text-muted-foreground font-medium">
                First Name
              </label>
              <p className="text-lg mt-1">{userInfo?.firstName || "N/A"}</p>
            </div>

            <div>
              <label className="text-sm text-muted-foreground font-medium">
                Last Name
              </label>
              <p className="text-lg mt-1">{userInfo?.lastName || "N/A"}</p>
            </div>

            {userInfo?.phoneNumber && (
              <div>
                <label className="text-sm text-muted-foreground font-medium">
                  Phone Number
                </label>
                <p className="text-lg mt-1">{userInfo.phoneNumber}</p>
              </div>
            )}

            {userInfo?.gender && (
              <div>
                <label className="text-sm text-muted-foreground font-medium">
                  Gender
                </label>
                <p className="text-lg mt-1 capitalize">
                  {userInfo.gender.toLowerCase()}
                </p>
              </div>
            )}

            {userInfo?.dob && (
              <div>
                <label className="text-sm text-muted-foreground font-medium">
                  Date of Birth
                </label>
                <p className="text-lg mt-1">
                  {new Date(userInfo.dob).toLocaleDateString("en-US", {
                    year: "numeric",
                    month: "long",
                    day: "numeric",
                  })}
                </p>
              </div>
            )}

            {userInfo?.address && (
              <div className="md:col-span-2">
                <label className="text-sm text-muted-foreground font-medium">
                  Address
                </label>
                <p className="text-lg mt-1">{userInfo.address}</p>
              </div>
            )}

            <div>
              <label className="text-sm text-muted-foreground font-medium">
                Account Type
              </label>
              <p className="text-lg mt-1 capitalize">
                {userInfo?.accountType || "N/A"}
              </p>
            </div>

            {userInfo?.noPassword && (
              <div className="md:col-span-2">
                <div className="bg-yellow-500/10 border border-yellow-500/20 rounded-lg p-4">
                  <p className="text-yellow-600 text-sm">
                    ⚠️ You signed up with Google. You can set a password to
                    enable email login.
                  </p>
                </div>
              </div>
            )}
          </div>

          <div className="mt-8 flex gap-4">
            <button
              onClick={() => setIsEditModalOpen(true)}
              className="px-6 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors"
            >
              Edit Profile
            </button>
            {userInfo?.noPassword && (
              <button className="px-6 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-700 transition-colors">
                Set Password
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Edit Profile Modal */}
      {isEditModalOpen && userInfo && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4"
          onClick={(e) => {
            if (e.target === e.currentTarget) setIsEditModalOpen(false);
          }}
        >
          <div className="bg-card border border-border rounded-2xl p-8 w-full max-w-2xl max-h-[90vh] overflow-y-auto relative">
            <button
              onClick={() => setIsEditModalOpen(false)}
              className="absolute top-4 right-4 text-muted-foreground hover:text-foreground"
              aria-label="Close modal"
            >
              <X size={24} />
            </button>

            <h2 className="text-2xl font-bold mb-6">Edit Profile</h2>

            {editError && (
              <div className="mb-4 p-3 rounded-lg bg-red-500/10 border border-red-500/20 text-red-600 text-sm">
                {editError}
              </div>
            )}

            <form onSubmit={handleEditProfile} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium mb-2">
                    First Name *
                  </label>
                  <input
                    type="text"
                    name="firstName"
                    defaultValue={userInfo.firstName}
                    className="w-full px-4 py-2 rounded-lg border border-border bg-background focus:outline-none focus:border-purple-500 transition-colors"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-2">
                    Last Name *
                  </label>
                  <input
                    type="text"
                    name="lastName"
                    defaultValue={userInfo.lastName}
                    className="w-full px-4 py-2 rounded-lg border border-border bg-background focus:outline-none focus:border-purple-500 transition-colors"
                    required
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium mb-2">
                    Phone Number
                  </label>
                  <input
                    type="tel"
                    name="phoneNumber"
                    defaultValue={userInfo.phoneNumber || ""}
                    placeholder="0979318414"
                    className="w-full px-4 py-2 rounded-lg border border-border bg-background focus:outline-none focus:border-purple-500 transition-colors"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-2">
                    Date of Birth
                  </label>
                  <input
                    type="date"
                    name="dob"
                    defaultValue={userInfo.dob || ""}
                    className="w-full px-4 py-2 rounded-lg border border-border bg-background focus:outline-none focus:border-purple-500 transition-colors"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium mb-2">Gender</label>
                <select
                  name="gender"
                  defaultValue={userInfo.gender || ""}
                  className="w-full px-4 py-2 rounded-lg border border-border bg-background focus:outline-none focus:border-purple-500 transition-colors"
                >
                  <option value="">Select gender</option>
                  <option value="MALE">Male</option>
                  <option value="FEMALE">Female</option>
                  <option value="OTHER">Other</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium mb-2">
                  Address
                </label>
                <input
                  type="text"
                  name="address"
                  defaultValue={userInfo.address || ""}
                  placeholder="Your address"
                  className="w-full px-4 py-2 rounded-lg border border-border bg-background focus:outline-none focus:border-purple-500 transition-colors"
                />
              </div>

              <div className="flex gap-4 mt-6">
                <button
                  type="submit"
                  disabled={isSaving}
                  className="flex-1 px-6 py-3 rounded-lg gradient-primary text-white font-semibold hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {isSaving ? "Saving..." : "Save Changes"}
                </button>
                <button
                  type="button"
                  onClick={() => setIsEditModalOpen(false)}
                  className="px-6 py-3 rounded-lg border border-border hover:bg-muted transition-colors"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
