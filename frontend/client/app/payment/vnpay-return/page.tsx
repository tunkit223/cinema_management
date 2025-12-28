'use client';

import { useEffect, useState } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';
import { CheckCircle, XCircle, Loader } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';

interface PaymentResult {
  code: string;
  message: string;
  txnRef?: string;
  amount?: number;
  orderInfo?: string;
  bookingId?: string;
}

export default function VNPayReturnPage() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [result, setResult] = useState<PaymentResult | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const handlePaymentCallback = async () => {
      try {
        // Get all params from URL
        const params = new URLSearchParams(searchParams);
        const responseCode = params.get('vnp_ResponseCode');
        const txnRef = params.get('vnp_TxnRef');
        const amount = params.get('vnp_Amount');
        const orderInfo = params.get('vnp_OrderInfo');

        // Log for debugging
        console.log('Payment Callback Params:', {
          responseCode,
          txnRef,
          amount,
          orderInfo,
        });

        // Call backend to verify and process payment
        const queryString = params.toString();
        const response = await fetch(
          `${process.env.NEXT_PUBLIC_API_URL}/payment/vnpay-return?${queryString}`,
          {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
            },
          }
        );

        if (!response.ok) {
          throw new Error(`Backend error: ${response.status}`);
        }

        const data = await response.json();
        
        // Try to get bookingId from sessionStorage (saved during booking process)
        if (!data.bookingId) {
          const savedBookingId = sessionStorage.getItem('current_booking_id');
          if (savedBookingId) {
            data.bookingId = savedBookingId;
            console.log('Retrieved bookingId from sessionStorage:', savedBookingId);
            // Clear it after reading to avoid reusing in future bookings
            sessionStorage.removeItem('current_booking_id');
          }
        }
        
        // Alternative: Use txnRef as fallback if still no bookingId
        if (!data.bookingId && txnRef) {
          data.bookingId = txnRef;
          console.log('Using txnRef as bookingId fallback:', txnRef);
        }
        
        setResult(data);

        // Log result
        console.log('Payment Result:', data);
        
        // Backend will automatically create tickets when payment is successful
      } catch (err) {
        console.error('Payment verification failed:', err);
        setError(
          err instanceof Error ? err.message : 'Có lỗi xảy ra khi xác thực thanh toán'
        );
      } finally {
        setLoading(false);
      }
    };

    if (searchParams.size > 0) {
      handlePaymentCallback();
    }
  }, [searchParams]);

  if (loading) {
    return (
      <div className="min-h-screen bg-background text-foreground flex items-center justify-center px-4 py-12">
        <Card className="w-full max-w-md text-center shadow-lg">
          <CardContent className="p-8">
          <Loader className="w-12 h-12 text-blue-600 mx-auto mb-4 animate-spin" />
          <h2 className="text-xl font-semibold text-gray-800 mb-2">
            Đang xác thực thanh toán...
          </h2>
          <p className="text-gray-600">
            Vui lòng chờ trong giây lát
          </p>
          </CardContent>
        </Card>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-background text-foreground flex items-center justify-center px-4 py-12">
        <Card className="w-full max-w-md text-center shadow-lg">
          <CardContent className="p-8">
          <XCircle className="w-16 h-16 text-red-600 mx-auto mb-4" />
          <h2 className="text-2xl font-bold text-red-600 mb-2">
            Lỗi xác thực
          </h2>
          <p className="text-gray-600 mb-6">
            {error}
          </p>
            <Button
              onClick={() => router.push('/booking')}
              className="w-full bg-red-600 hover:bg-red-700"
            >
              Quay lại đặt vé
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  const isSuccess = result?.code === '00';

  return (
    <div className="min-h-screen bg-background text-foreground flex items-center justify-center px-4 py-12">
      <Card className="w-full max-w-md text-center shadow-lg">
        <CardHeader className="pb-4">
          <CardTitle className="flex items-center justify-center gap-2 text-lg text-gray-700">
            <Badge variant={isSuccess ? 'default' : 'destructive'} className="uppercase">
              {isSuccess ? 'Thành công' : 'Thất bại'}
            </Badge>
            <span className="text-sm text-gray-500">VNPay</span>
          </CardTitle>
        </CardHeader>
        <CardContent className="pt-0 pb-6">
        {isSuccess ? (
          <>
            <CheckCircle className="w-16 h-16 text-green-600 mx-auto mb-4" />
            <h2 className="text-2xl font-bold text-green-600 mb-2">
              Thanh toán thành công!
            </h2>
            <p className="text-gray-600 mb-6">
              {result?.message}
            </p>

            <div className="bg-gray-50 rounded-lg p-4 mb-6 text-left space-y-3">
              {result?.txnRef && (
                <div>
                  <p className="text-xs uppercase tracking-wide text-gray-500">Mã giao dịch</p>
                  <p className="text-base font-semibold text-gray-800 break-all">
                    {result.txnRef}
                  </p>
                </div>
              )}
              {result?.amount && (
                <div>
                  <p className="text-xs uppercase tracking-wide text-gray-500">Số tiền</p>
                  <p className="text-lg font-semibold text-gray-900">
                    {result.amount.toLocaleString('vi-VN')} ₫
                  </p>
                </div>
              )}
              {result?.orderInfo && (
                <div>
                  <p className="text-xs uppercase tracking-wide text-gray-500">Nội dung</p>
                  <p className="text-base font-semibold text-gray-800">
                    {result.orderInfo}
                  </p>
                </div>
              )}
            </div>

            <div className="space-y-3">
              <Button
                onClick={() => {
                  if (result?.bookingId) {
                    console.log('Navigating to success page with bookingId:', result.bookingId);
                    router.push(`/booking/success/${result.bookingId}`);
                  } else {
                    console.warn('No bookingId found, redirecting to my-tickets');
                    router.push('/my-tickets');
                  }
                }}
                className="w-full bg-green-600 hover:bg-green-700"
              >
                Xem vé của tôi
              </Button>
              <Button
                variant="secondary"
                onClick={() => router.push('/')}
                className="w-full"
              >
                Quay về trang chủ
              </Button>
            </div>
          </>
        ) : (
          <>
            <XCircle className="w-16 h-16 text-red-600 mx-auto mb-4" />
            <h2 className="text-2xl font-bold text-red-600 mb-2">
              Thanh toán thất bại
            </h2>
            <p className="text-gray-600 mb-6">
              {result?.message || 'Giao dịch không thành công'}
            </p>

            {result?.txnRef && (
              <div className="bg-gray-50 rounded-lg p-4 mb-6 text-left">
                <p className="text-xs uppercase tracking-wide text-gray-500">Mã giao dịch</p>
                <p className="text-base font-semibold text-gray-800 break-all">
                  {result.txnRef}
                </p>
              </div>
            )}

            <div className="space-y-3">
              <Button
                onClick={() => router.push('/booking')}
                className="w-full bg-orange-600 hover:bg-orange-700 text-white"
              >
                Thử lại
              </Button>
              <Button
                variant="secondary"
                onClick={() => router.push('/')}
                className="w-full"
              >
                Quay về trang chủ
              </Button>
            </div>
          </>
        )}
        </CardContent>
      </Card>
    </div>
  );
}
