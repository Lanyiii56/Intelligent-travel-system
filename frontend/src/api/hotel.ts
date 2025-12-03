/**
 * 酒店模块 API
 */
import request from './request';

// 类型定义
export interface Hotel {
  id: number;
  name: string;
  description: string;
  regionId: number;
  address: string;
  priceMin: number;
  priceMax: number;
  rating: number;
  stars: number;
  latitude: number;
  longitude: number;
  imageUrl: string;
  facilities: string;
  phone: string;
  checkInTime: string;
  checkOutTime: string;
  // 外部预订链接
  ctripUrl?: string;
  meituanUrl?: string;
  qunarUrl?: string;
  bookingUrl?: string;
}

export interface BookingLinks {
  ctrip?: string;
  meituan?: string;
  qunar?: string;
  booking?: string;
}

export interface HotelRoom {
  id: number;
  hotelId: number;
  name: string;
  description: string;
  price: number;
  bedType: number;  // 1-大床, 2-双床, 3-单床
  maxGuests: number;
  area: number;
  hasWindow: boolean;
  hasBreakfast: boolean;
  canCancel: boolean;
  stock: number;
  imageUrl: string;
  facilities: string;
}

export interface HotelOrder {
  id?: number;
  userId: number;
  hotelId: number;
  roomId?: number;
  checkInDate: string;
  checkOutDate: string;
  nights?: number;
  roomCount: number;
  guestCount: number;
  guestName: string;
  guestPhone: string;
  totalPrice?: number;
  status?: string;
  createdAt?: string;
  remark?: string;
  // 外部订单信息
  externalPlatform?: string;
  externalOrderId?: string;
  externalOrderUrl?: string;
  confirmationCode?: string;
}

export interface ExternalOrderData {
  userId: number;
  hotelId: number;
  roomId?: number;
  checkInDate: string;
  checkOutDate: string;
  roomCount?: number;
  guestCount?: number;
  guestName?: string;
  guestPhone?: string;
  totalPrice?: number;
  platform: string;
  externalOrderId?: string;
  externalOrderUrl?: string;
  confirmationCode?: string;
  remark?: string;
}

export interface HotelSearchParams {
  regionId?: number;
  keyword?: string;
  stars?: number;
  minPrice?: number;
  maxPrice?: number;
  sort?: 'rating' | 'price';
}

// API 方法

// 获取酒店列表
export function getHotels(params?: HotelSearchParams) {
  return request.get<Hotel[]>('/hotels', { params });
}

// 获取酒店详情
export function getHotelById(id: number) {
  return request.get<Hotel>(`/hotels/${id}`);
}

// 获取附近酒店
export function getNearbyHotels(latitude: number, longitude: number, radius: number = 5) {
  return request.get<Hotel[]>('/hotels/nearby', {
    params: { latitude, longitude, radius }
  });
}

// 获取酒店房型
export function getHotelRooms(hotelId: number, availableOnly: boolean = false) {
  return request.get<HotelRoom[]>(`/hotels/${hotelId}/rooms`, {
    params: { availableOnly }
  });
}

// 获取房型详情
export function getRoomById(roomId: number) {
  return request.get<HotelRoom>(`/hotels/rooms/${roomId}`);
}

// 创建订单
export function createHotelOrder(order: HotelOrder) {
  return request.post<HotelOrder>('/hotels/orders', order);
}

// 获取用户订单
export function getUserHotelOrders(userId: number) {
  return request.get<HotelOrder[]>(`/hotels/orders/user/${userId}`);
}

// 获取订单详情
export function getHotelOrderById(orderId: number) {
  return request.get<HotelOrder>(`/hotels/orders/${orderId}`);
}

// 支付订单
export function payHotelOrder(orderId: number) {
  return request.post<HotelOrder>(`/hotels/orders/${orderId}/pay`);
}

// 取消订单
export function cancelHotelOrder(orderId: number) {
  return request.post<HotelOrder>(`/hotels/orders/${orderId}/cancel`);
}

// 获取酒店外部预订链接
export function getBookingLinks(hotelId: number, checkIn?: string, checkOut?: string) {
  return request.get<BookingLinks>(`/hotels/${hotelId}/booking-links`, {
    params: { checkIn, checkOut }
  });
}

// 创建外部预订记录
export function createExternalOrder(orderData: ExternalOrderData) {
  return request.post<HotelOrder>('/hotels/orders/external', orderData);
}

// 更新外部订单信息
export function updateExternalOrder(orderId: number, updateData: {
  externalOrderId?: string;
  externalOrderUrl?: string;
  confirmationCode?: string;
  status?: string;
}) {
  return request.post<HotelOrder>(`/hotels/orders/${orderId}/external-update`, updateData);
}

// 辅助函数
export function getBedTypeName(bedType: number): string {
  const types: Record<number, string> = {
    1: '大床',
    2: '双床',
    3: '单床'
  };
  return types[bedType] || '未知';
}

export function getStarsText(stars: number): string {
  return '★'.repeat(stars) + '☆'.repeat(5 - stars);
}

export function getOrderStatusText(status: string): string {
  const statusMap: Record<string, string> = {
    'PENDING': '待支付',
    'PAID': '已支付',
    'CONFIRMED': '已确认',
    'CHECKED_IN': '已入住',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  };
  return statusMap[status] || status;
}

export function getOrderStatusClass(status: string): string {
  const classMap: Record<string, string> = {
    'PENDING': 'status-pending',
    'PAID': 'status-paid',
    'CONFIRMED': 'status-confirmed',
    'CHECKED_IN': 'status-checkedin',
    'COMPLETED': 'status-completed',
    'CANCELLED': 'status-cancelled'
  };
  return classMap[status] || '';
}
