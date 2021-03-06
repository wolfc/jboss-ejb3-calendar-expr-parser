/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ejb3.timer.schedule;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.ejb.ScheduleExpression;

/**
 * ScheduleExpressionParserImpl
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class CalendarBasedTimeout
{

   /**
    * The {@link ScheduleExpression} from which this {@link CalendarBasedTimeout}
    * was created
    */
   private ScheduleExpression scheduleExpression;

   /**
    * The {@link Second} created out of the {@link ScheduleExpression#getSecond()} value
    */
   private Second second;

   /**
    * The {@link Minute} created out of the {@link ScheduleExpression#getMinute()} value
    */
   private Minute minute;
   
   /**
    * The {@link Hour} created out of the {@link ScheduleExpression#getHour()} value
    */
   private Hour hour;
   
   /**
    * The {@link DayOfWeek} created out of the {@link ScheduleExpression#getDayOfWeek()} value
    */
   private DayOfWeek dayOfWeek;
   
   /**
    * The {@link DayOfMonth} created out of the {@link ScheduleExpression#getDayOfMonth()} value
    */
   private DayOfMonth dayOfMonth;
   
   /**
    * The {@link Month} created out of the {@link ScheduleExpression#getMonth()} value
    */
   private Month month;
   
   /**
    * The {@link Year} created out of the {@link ScheduleExpression#getYear()} value
    */
   private Year year;
   
   /**
    * The first timeout relative to the time when this {@link CalendarBasedTimeout} was created
    * from a {@link ScheduleExpression} 
    */
   private Calendar firstTimeout;

   /**
    * Creates a {@link CalendarBasedTimeout} from the passed <code>schedule</code>.
    * <p>
    *   This constructor parses the passed {@link ScheduleExpression} and sets up
    *   its internal representation of the same.
    * </p>
    * @param schedule The schedule 
    */
   public CalendarBasedTimeout(ScheduleExpression schedule)
   {
      // store the original expression from which this
      // CalendarBasedTimeout was created
      this.scheduleExpression = schedule;
      
      // Start parsing the values in the ScheduleExpression
      this.second = new Second(schedule.getSecond());
      this.minute = new Minute(schedule.getMinute());
      this.hour = new Hour(schedule.getHour());
      this.dayOfWeek = new DayOfWeek(schedule.getDayOfWeek());
      this.dayOfMonth = new DayOfMonth(schedule.getDayOfMonth());
      this.month = new Month(schedule.getMonth());
      this.year = new Year(schedule.getYear());
      
      // Now that we have parsed the values from the ScheduleExpression,
      // determine and set the first timeout (relative to the current time)
      // of this CalendarBasedTimeout
      this.setFirstTimeout();
   }

   public Calendar getNextTimeout(Calendar current)
   {
      Calendar next = new GregorianCalendar(current.getTimeZone());
      next.setTime(current.getTime());
      next.setFirstDayOfWeek(Calendar.SUNDAY);

      // increment the current second by 1
      next.add(Calendar.SECOND, 1);

      next = this.second.getNextSecond(next);
//      next.getTime();
      next = this.minute.getNextMinute(next);
  //    next.getTime();
      next = this.hour.getNextHour(next);
    //  next.getTime();
      next = this.dayOfWeek.getNextDayOfWeek(next);
      //next.getTime();
      next = this.dayOfMonth.getNextDayOfMonth(next);
     // next.getTime();
      next = this.month.getNextMonth(next);
      if (next == null)
      {
         return null;
      }
      // next.getTime();
      next = this.year.getNextYear(next);
      //next.getTime();
      return next;
   }

   /**
    * 
    * @return
    */
   public Calendar getFirstTimeout()
   {
      return this.firstTimeout;
   }
   
   private void setFirstTimeout()
   {
      this.firstTimeout = new GregorianCalendar();
      this.firstTimeout.set(Calendar.HOUR, 0);
      this.firstTimeout.set(Calendar.MINUTE, 0);
      this.firstTimeout.set(Calendar.SECOND, 0);
      this.firstTimeout.setFirstDayOfWeek(Calendar.SUNDAY);

      this.firstTimeout = this.second.getNextSecond(this.firstTimeout);
      this.firstTimeout = this.minute.getNextMinute(this.firstTimeout);
      this.firstTimeout = this.hour.getNextHour(this.firstTimeout);
      this.firstTimeout = this.dayOfWeek.getNextDayOfWeek(this.firstTimeout);
      this.firstTimeout = this.dayOfMonth.getNextDayOfMonth(this.firstTimeout);
      this.firstTimeout = this.month.getNextMonth(this.firstTimeout);
      if (this.firstTimeout != null)
      {
         this.firstTimeout = this.year.getNextYear(this.firstTimeout);
      }
      
   }
   
   /**
    * Returns the original {@link ScheduleExpression} from which this {@link CalendarBasedTimeout}
    * was created.
    * 
    * @return
    */
   public ScheduleExpression getScheduleExpression()
   {
      return this.scheduleExpression;
   }
}
