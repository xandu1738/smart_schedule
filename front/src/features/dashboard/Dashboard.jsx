import React from 'react'
import ScheduleDashCard from '../../components/ScheduleDashCard'
import RecentActivity from '../../components/RecentActivity'
import DashboardLayout from '../../components/layout/DashboardLayout'
import DashboardCard from '../../components/DashboardCard'
import { Building, Users, Clock, Calendar } from 'lucide-react'
import QuickActions from '../../components/QuickActions'



const Dashboard = () => {
  return (
    <section className="m-8">
      <div className="flex flex-col items-start justify-start left-0 w-full">
        <h1 className="font-bold text-3xl">Dashboard</h1>
        <p className='text-sm text-gray-500 pt-2'>Welcome back! Here's what's happening in your organization.</p>
      </div>

      <section className='mt-8 grid md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4'>
        <DashboardCard title={"Total Departments"} value={8} icon={<Building />} stat={12}  />
        <DashboardCard title={"Active Employees"} value={124} icon={<Users />} stat={12}  />
        <DashboardCard title={"Active Shifts"} value={15} icon={<Clock />} stat={12}  />
        <DashboardCard title={"Schedules This Week"} value={45} icon={<Calendar />} stat={12}  />
      </section>
      <section className='grid grid-cols-1 2xl:grid-cols-2 gap-4'>
        <RecentActivity />
        <QuickActions />
      </section>
      
    </section>
  )
}

export default Dashboard